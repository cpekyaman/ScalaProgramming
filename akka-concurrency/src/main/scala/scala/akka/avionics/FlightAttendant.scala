package scala.akka.avionics

import akka.actor.{Actor}
import scala.concurrent.duration._

trait AttendantResponsiveness {
	val maxResponseTime: Int
	def responseDuration = scala.util.Random.nextInt(maxResponseTime).millis
}

object FlightAttendant {
    case class GetDrink(drinkName: String)
    case class Drink(drinkName: String)
    
    def apply() = 
        new FlightAttendant with AttendantResponsiveness {
            val maxResponseTime = 30000
        }
}

class FlightAttendant extends Actor {
    this: AttendantResponsiveness =>
    import FlightAttendant._
    // bring the execution context into implicit scope for the scheduler below
    implicit val ec = context.dispatcher
    
    def receive = {
        case GetDrink(drinkname) =>
            // We don't respond right away, but use the scheduler to ensure we do eventually
            context.system.scheduler.scheduleOnce(responseDuration, sender, Drink(drinkname))
    }
}
