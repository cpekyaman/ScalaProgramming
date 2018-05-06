package scala.akka.stateful

// Imports to help us create Actors, plus logging
import akka.actor.{Actor, ActorRef}
import scala.akka.avionics.{Altimeter}

object Controller {
    // amount is a value between -1 and 1. The altimeter
    // ensures that any value outside that range is truncated
    // to be within it.
    case class StickBack(amount: Float)
    case class StickForward(amount: Float)
    
    case class StickLeft(amount: Float)
    case class StickRight(amount: Float)
    
    case class HasControl(entity: ActorRef)
}

class Controller(plane: ActorRef, altimeter: ActorRef, heading: ActorRef) extends Actor {
    import Controller._
    import Altimeter._
    import HeadingIndicator._
    
    def receive = controlledBy(context.system.deadLetters)
    
    def controlledBy(pilot: ActorRef): Receive = {
        // Pilot pulled the stick back by a certain amount,
        // and we inform the Altimeter that we're climbing
        case StickBack(amount) if sender == pilot => altimeter ! RateChange(amount)
        
        // Pilot pushes the stick forward and we inform the
        // Altimeter that we're descending
        case StickForward(amount) if sender == pilot => altimeter ! RateChange(-1 * amount)
        
        case StickLeft(amount) if sender == pilot => heading ! BankChange(-1 * amount)
        
        case StickRight(amount) if sender == pilot => heading ! BankChange(amount)
        
        case HasControl(entity) if sender == plane => context.become(controlledBy(entity))
    }
}
