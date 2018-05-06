package scala.akka.routing

import akka.actor.{Actor, ActorRef, ActorLogging}
import scala.concurrent.duration._

object Passenger {
	case object FastenSeatbelts
	case object UnfastenSeatbelts
	
	val SeatAssignment = """([\w\s_]+)-(\d+)-([A-Z])""".r
}

trait DrinkRequestProbability {
    val askThreshold = 0.9f
    
    val requestMin = 20.minutes
    val requestUpper = 30.minutes
    
    def randomishTime(): FiniteDuration = 
        requestMin + scala.util.Random.nextInt(requestUpper.toMillis.toInt).millis
}

trait PassengerProvider {
    def newPassenger(callButton: ActorRef, bathrooms: ActorRef): Actor = 
        new Passenger(callButton, bathrooms) with DrinkRequestProbability
}

class Passenger(callButton: ActorRef, bathrooms: ActorRef) extends Actor with ActorLogging {
    this: DrinkRequestProbability =>
    
    import Passenger._
    import scala.akka.avionics.FlightAttendant.{GetDrink, Drink}
    import scala.collection.JavaConverters._
    
    val r = scala.util.Random
    val scheduler = context.system.scheduler
    implicit val ec = context.dispatcher
    
    case object CallForDrink
    
    // The name of the Passenger can't have spaces in it,
    // since that's not a valid character in the URI spec. 
    // We know the name will have underscores in place of spaces,
    // and we'll convert those back here.
    val SeatAssignment(myname, _, _) = self.path.name.replaceAllLiterally("_", " ")
    val drinks = context.system.settings.config.getStringList("scala.akka.avionics.drinks").asScala.toIndexedSeq    
    
    override def preStart() {
        self ! CallForDrink
    }
    
    def maybeSendDrinkRequest(): Unit = {
        if(r.nextFloat() > askThreshold) {
            val drink = drinks(r.nextInt(drinks.length))
            callButton ! GetDrink(drink)
        }
        scheduler.scheduleOnce(randomishTime(), self, CallForDrink)
    }
    
    def receive = {
        case CallForDrink => maybeSendDrinkRequest()
        case Drink(drink) => log.info(s"$myname received drink $drink")
        case FastenSeatbelts => log.info(s"$myname fastening seatbelt")
        case UnfastenSeatbelts => log.info(s"$myname unfastening seatbelt")
    }
}


