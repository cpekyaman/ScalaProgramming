package scala.akka.stateful

import akka.actor.{Actor,ActorRef}
import scala.akka.avionics.{CoPilot,AutoPilot}
import fsm.FlyingProvider

trait PilotProvider {
	def newPilot(plane: ActorRef, altimeter: ActorRef, heading: ActorRef): Actor = 
	    new Pilot(plane,altimeter,heading) with DrinkingProvider with FlyingProvider
	def newCoPilot(plane: ActorRef): Actor = new CoPilot(plane)	
	def newAutoPilot(plane: ActorRef): Actor = new AutoPilot(plane)
}
