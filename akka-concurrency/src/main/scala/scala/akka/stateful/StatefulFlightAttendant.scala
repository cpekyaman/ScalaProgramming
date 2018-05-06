package scala.akka.stateful

import scala.akka.avionics.{FlightAttendant, AttendantResponsiveness}
import akka.actor.{ActorRef, Actor, Cancellable}

object StatefulFlightAttendant {
    case class Assist(passenger: ActorRef)
	case object Busy_?
    case object Yes
	case object No
	
	def apply() = 
        new StatefulFlightAttendant with AttendantResponsiveness {
            val maxResponseTime = 30000
        }
}

trait FlightAttendantProvider {
    def newFlightAttendant(): Actor = StatefulFlightAttendant()
}

class StatefulFlightAttendant extends Actor {
     this: AttendantResponsiveness =>
     
     import FlightAttendant._
     import StatefulFlightAttendant._
     
     // bring the execution context into implicit scope for the scheduler below
    implicit val ec = context.dispatcher    
      
    // internal message
    case class DeliverDrink(drink: Drink)
    
    // Stores our timer, which is an instance of 'Cancellable'
    var pendingDelivery: Option[Cancellable] = None
    
    def scheduleDelivery(drinkName: String): Cancellable = {         
         context.system.scheduler.scheduleOnce(responseDuration, self, DeliverDrink(Drink(drinkName)))
    }
    // If we have an injured passenger, then we need to
    // immediately assist them, by giving them the 'secret'
    // Magic Healing Potion that's available on all flights in
    // and out of Xanadu
    def assistInjuredPassenger: Receive = {
        case Assist(passenger) =>
            pendingDelivery foreach { _.cancel() }
            pendingDelivery = None
            passenger ! Drink("Healing Potion")
    }
    
    // This general handler is responsible for servicing drink
    // requests when we're not busy servicing an existing request
    def handleDrinkRequests: Receive = {
        case GetDrink(drinkName) =>
            pendingDelivery = Some(scheduleDelivery(drinkName))
            context.become(assistInjuredPassenger orElse handleSpecificPerson(sender))
        case Busy_? => sender ! No            
    }
    
    // When we are already busy getting a drink for someone then we move to this state
    def handleSpecificPerson(person: ActorRef): Receive = {
        // If the person asking us for a drink is the same
        // person we're currently handling then we'll cancel
        // what we were doing and service their new request
        case GetDrink(drinkName) if sender == person =>
            pendingDelivery foreach { _.cancel() }
            pendingDelivery = Some(scheduleDelivery(drinkName))
        
        // deliver drink only when serving specific passenger
        case DeliverDrink(drink) =>
            person ! drink
            pendingDelivery = None
            context.become(assistInjuredPassenger orElse handleDrinkRequests)
        
        // I'm already busy, forward to the lead attendant
        case m: GetDrink => context.parent forward m
        
        // Tell the sender I'm busy
        case Busy_? => sender ! Yes
    }

    def receive = assistInjuredPassenger orElse handleDrinkRequests
}
