package scala.akka.supervision.avionics.actor

import scala.akka.avionics.{Controller,Altimeter,EventSource,Pilot}
import akka.actor.{Actor,ActorLogging,Props,ActorRef}
import scala.akka.supervision._
import scala.akka.supervision.avionics.provider.{PilotProvider, LeadFlightAttendantProvider, AltimeterProvider}

import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.Await
import scala.concurrent.duration._

class Plane extends Actor with ActorLogging {
    this : AltimeterProvider with PilotProvider with LeadFlightAttendantProvider => 
    
    import Altimeter._
    import EventSource._
    import Pilot._
    import scala.akka.avionics.Plane._
    
    import scala.akka.supervision.IsolatedLifecycleSupervisor._
    
    private val config = context.system.settings.config
    private val configRoot = "scala.akka.avionics.crew"
    
    private val pilotName = config.getString(s"$configRoot.pilot")
    private val copilotName = config.getString(s"$configRoot.copilot")
    private val leadAttendantName = config.getString(s"$configRoot.leadAttendant")
    
    implicit val askTimeout = Timeout(1.second)
    
    def startEquipment() {
        val controls = context.actorOf(Props(equipmentSupervisor), "Equipment")
        Await.result(controls ? WaitForStart, 1.second)
    }    
    private def equipmentSupervisor: Actor = 
        new IsolatedResumeSupervisor with OneForOneStrategyFactory {
            def childStarter(): Unit = {
                val altimeter = context.actorOf(Props(newAltimeter), "Altimeter")
                context.actorOf(Props(newAutoPilot(self)), "AutoPilot")
                context.actorOf(Props(new Controller(altimeter)), "Controller")
            }
        }
    private def equipmentActor(name: String): ActorRef = context.actorFor("Equipment/" + name)    
    
    def startCrew() {
        val pilots = context.actorOf(Props(pilotSupervisor), "Pilots")        
        // Use the default strategy here, which restarts indefinitely
        context.actorOf(Props(newLeadAttendant), leadAttendantName)
        Await.result(pilots ? WaitForStart, 1.second)
    }
    private def pilotSupervisor: Actor =
        new IsolatedStopSupervisor with OneForOneStrategyFactory {
            def childStarter(): Unit = {
                context.actorOf(Props(newPilot(self)), pilotName)
                context.actorOf(Props(newCoPilot(self)), copilotName)
            }
        }
    private def pilotActor(name: String): ActorRef = context.actorFor("Pilots/" + name)
    
    override def preStart() {
        startEquipment()
        startCrew()

        equipmentActor("Altimeter") ! Register(self)
        List(pilotActor(pilotName), pilotActor(copilotName)) foreach { _ ! ReadyToGo }
    }
    
    def receive = {
        case GiveMeControl =>
            log info("Plane giving control.")
            sender ! Controls(equipmentActor("Controller"))
        case AltitudeChange(altitude) =>
            log info(s"Altitude is now: $altitude")
    }
}
