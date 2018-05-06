package scala.akka.avionics

// Imports to help us create Actors, plus logging
import akka.actor.{Actor, ActorRef}

object Controller {
    // amount is a value between -1 and 1. The altimeter
    // ensures that any value outside that range is truncated
    // to be within it.
    case class StickBack(amount: Float)
    case class StickForward(amount: Float)
}

class Controller(altimeter: ActorRef) extends Actor {
    import Controller._
    import Altimeter._
    
    def receive = {
        // Pilot pulled the stick back by a certain amount,
        // and we inform the Altimeter that we're climbing
        case StickBack(amount) => altimeter ! RateChange(amount)
        
        // Pilot pushes the stick forward and we inform the
        // Altimeter that we're descending
        case StickForward(amount) => altimeter ! RateChange(-1 * amount)
    }
}
