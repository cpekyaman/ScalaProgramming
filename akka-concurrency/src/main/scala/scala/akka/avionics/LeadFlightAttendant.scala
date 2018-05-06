package scala.akka.avionics

import akka.actor.{Actor, ActorRef, Props}

trait AttendantCreationPolicy {
	val numberOfAttendants: Int = 8
	def createAttendant: Actor = FlightAttendant() 
}

object LeadFlightAttendant {
	case object GetFlightAttendant
	case class Attendant(a: ActorRef)
	
	def apply() = new LeadFlightAttendant with AttendantCreationPolicy
}

class LeadFlightAttendant extends Actor {
    this: AttendantCreationPolicy =>
    import LeadFlightAttendant._
    // After we've successfully spooled up the
    // LeadFlightAttendant, we're going to have it create all of
    // its subordinates
    override def preStart() {
        import scala.collection.JavaConverters._
        val attendantNames = context.system.settings.config.getStringList("scala.akka.avionics.crew.attendants").asScala
        attendantNames take numberOfAttendants foreach { name => 
            context.actorOf(Props(createAttendant), name)
        }
    }
    
    private def randomAttendant(): ActorRef = 
        context.children.take(scala.util.Random.nextInt(numberOfAttendants) + 1).last
    
    def receive = {
        case GetFlightAttendant => sender ! Attendant(randomAttendant())
        case m => randomAttendant() forward m
    }
}
