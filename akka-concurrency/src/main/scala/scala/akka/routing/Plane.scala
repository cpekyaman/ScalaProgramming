package scala.akka.routing

import scala.akka.avionics.{Altimeter,EventSource}
import akka.actor.{Actor,ActorLogging,Props,ActorRef,OneForOneStrategy}
import akka.agent.Agent
import scala.akka.supervision._
import scala.akka.supervision.avionics.provider.AltimeterProvider
import scala.akka.stateful.{PilotProvider, FlightAttendantProvider, Controller, HeadingIndicator}

import akka.pattern.ask
import akka.util.Timeout
import akka.routing.{FromConfig,RoundRobinPool}
import scala.concurrent.Await
import scala.concurrent.duration._

object Plane {
    def apply() = new Plane with AltimeterProvider with PilotProvider with FlightAttendantProvider
}

class Plane extends Actor with ActorLogging {
    this : AltimeterProvider with PilotProvider with FlightAttendantProvider => 
    
    import scala.akka.util.ActorUtil.actorFor
    import Altimeter._
    import HeadingIndicator._
    import EventSource._    
    import scala.akka.stateful.Plane._
    
    import scala.akka.avionics.Pilot._
    import scala.akka.avionics.Plane._
    
    import scala.akka.supervision.IsolatedLifecycleSupervisor._
    
    import scala.akka.agents._
    import akka.actor.SupervisorStrategy._
    
    private val config = context.system.settings.config
    private val configRoot = "scala.akka.avionics.crew"
    
    private val pilotName = config.getString(s"$configRoot.pilot")
    private val copilotName = config.getString(s"$configRoot.copilot")
    private val leadAttendantName = config.getString(s"$configRoot.leadAttendant")
    
    implicit val askTimeout = Timeout(1.second)
    
    val maleBathroomCounter = Agent(GenderAndTime(Male, 0.seconds, 0))(context.system.dispatcher)
    val femaleBathroomCounter = Agent(GenderAndTime(Female, 0.seconds, 0))(context.system.dispatcher)
    
    private def startUtilities() {
        context.actorOf(
            Props(new Bathroom(femaleBathroomCounter, maleBathroomCounter))
            .withRouter(RoundRobinPool(nrOfInstances=4, supervisorStrategy = OneForOneStrategy(){
                case _ => Resume
            }))
            , "Bathrooms"
        )
    }
    
    def startEquipment() {
        val controls = context.actorOf(Props(equipmentSupervisor), "Equipment")
        Await.result(controls ? WaitForStart, 1.second)
    }    
    private def equipmentSupervisor: Actor = 
        new IsolatedResumeSupervisor with OneForOneStrategyFactory {
            def childStarter(): Unit = {
                val altimeter = context.actorOf(Props(newAltimeter), "Altimeter")
                val heading = context.actorOf(Props(HeadingIndicator()), "HeadingIndicator")
                context.actorOf(Props(newAutoPilot(self)), "AutoPilot")
                context.actorOf(Props(new Controller(self, altimeter, heading)), "Controller")                
            }
        }
    private def equipmentActor(name: String): ActorRef = actorFor(context, "Equipment/" + name)    
    
    def startCrew() {
        val leadAttendant = context.actorOf(Props(newFlightAttendant).withRouter(FromConfig()), "FlightAttendantRouter")
        val pilots = context.actorOf(Props(pilotSupervisor), "Pilots")        
        // Use the default strategy here, which restarts indefinitely        
        Await.result(pilots ? WaitForStart, 1.second)
    }
    private def pilotSupervisor: Actor =
        new IsolatedStopSupervisor with OneForOneStrategyFactory {
            def childStarter(): Unit = {
                context.actorOf(Props(newPilot(self, equipmentActor("Altimeter"), equipmentActor("HeadingIndicator"))), pilotName)
                context.actorOf(Props(newCoPilot(self)), copilotName)
            }
        }
    private def pilotActor(name: String): ActorRef = actorFor(context, "Pilots/" + name)
    
    private def startPeople() {
        val bathrooms = actorFor(context, "Bathrooms")
        
        val people = context.actorOf(Props(new IsolatedStopSupervisor with OneForOneStrategyFactory {
            def childStarter() {
                context.actorOf(Props(PassengerSupervisor(actorFor(context, "FlightAttendantRouter"), bathrooms)), "Passengers")
            }
        }))
    } 
    
    override def preStart() {
        startEquipment()
        startUtilities()
        startCrew()
        startPeople()

        // equipmentActor("Altimeter") ! Register(self)
        // equipmentActor("HeadingIndicator") ! Register(self)
        List(pilotActor(pilotName), pilotActor(copilotName)) foreach { _ ! ReadyToGo }
    }
    
    override def postStop() {
        // Await the values. If the plane is shutting down then,
        // most likely, the whole system is going down. If we get
        // a Future result to the values then we may not get them
        // until the system has disappeared. Bad news
        val male = 	Await.result(maleBathroomCounter.future, 1.seconds) 
        val female = Await.result(femaleBathroomCounter.future, 1.seconds)
        
        log.info(s"${male.count} men used the bathroom")
        log.info(s"${female.count} women used the bathroom")
        log.info("peak bathroom usage time for men " + s"was ${male.peakDuration}")
        log.info("peak bathroom usage time for women " + s"was ${female.peakDuration}")
    }
    
    def receive = {
        case GiveMeControl =>
            log info("Plane giving control.")
            sender ! Controls(equipmentActor("Controller"))
        case AltitudeChange(altitude) =>
            log info(s"Altitude is now: $altitude")
        case HeadingUpdate(heading) =>
            log info(s"Heading is now: $heading")
        case LostControl =>
           pilotActor("AutoPilot") ! TakeControl 
    }
}
