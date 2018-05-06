package scala.akka.routing

import scala.akka.stateful.FlightAttendantProvider

import akka.routing.{RouterConfig, RouteeProvider, Route, Destination}
import akka.dispatch.Dispatchers
import akka.actor.{SupervisorStrategy, Props}

class SectionSpecificAttendantRouter extends RouterConfig {
	this: FlightAttendantProvider =>
	
	// The RouterConfig requires us to fill out these two
	// fields. We know what the supervisorStrategy is but we're
	// only slightly aware of the Dispatcher, which will be
	// discussed in detail later
	def routerDispatcher: String = Dispatchers.DefaultDispatcherId
	def supervisorStrategy: SupervisorStrategy = SupervisorStrategy.defaultStrategy
	
	// The createRoute method is what invokes the decision
    // making code. We instantiate the Actors we need and then
    // create the routing code
    def createRoute(provider: RouteeProvider): Route = {
        // Create 5 flight attendants
        val attendants = (1 to 5) map { n =>
            provider.context.actorOf(Props(newFlightAttendant), "Attendant-" + n)
        }
        
        // Register them with the provider - This is important.
        // If you forget to do this, nobody's really going to tell you about it :)
        provider.registerRoutees(attendants)
        
        // Now the partial function that calculates the route.
        // We are going to route based on the name of the
        // incoming sender. Of course, you would cache this or
        // do something slicker.
        {
            case (sender,message) =>
                import Passenger.SeatAssignment
                val SeatAssignment(_,row,_) = sender.path.name
                List(Destination(sender, attendants(math.floor(row.toInt / 11).toInt)))
        }
    }
}
