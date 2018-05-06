package scala.akka.avionics

// Imports to help us create Actors, plus logging
import akka.actor.{Props, Actor, ActorRef, ActorLogging}

object Plane {
    case object GiveMeControl
    case class Controls(a: ActorRef)
}

// We want the Plane to own the Altimeter and we're going to
// do that by passing in a specific factory we can use to
// build the Altimeter
class Plane extends Actor with ActorLogging {
    import Altimeter._
    import Plane._
    import EventSource._
    import Pilot._
    
    private val config = context.system.settings.config
    private val configRoot = "scala.akka.avionics.crew"
    
    private val altimeter = context.actorOf(Props(Altimeter()), "Altimeter")
    private val controller = context.actorOf(Props(new Controller(altimeter)), "Controller")
    
    private def createChild(p: Props, name: String): ActorRef =
       context.actorOf(p, config.getString(s"$configRoot.$name")) 
    
    private val pilot = createChild(Props[Pilot], "pilot")
    private val copilot = createChild(Props[CoPilot], "copilot")    
    private val flightAttendant = createChild(Props(LeadFlightAttendant()), "leadAttendant")
    
    private val autopilot = context.actorOf(Props[AutoPilot], "AutoPilot")
   
    override def preStart() {
        altimeter ! Register(self)
        List(pilot, copilot) foreach { _ ! ReadyToGo }
    }

    def receive = {
        case GiveMeControl =>
            log info("Plane giving control.")
            sender ! Controls(controller)
        case AltitudeChange(altitude) =>
            log info(s"Altitude is now: $altitude")
    }
}
