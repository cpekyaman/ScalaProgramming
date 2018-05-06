package scala.akka.supervision.avionics.provider

import akka.actor.{Actor,ActorRef}
import scala.akka.avionics.{Pilot,CoPilot,AutoPilot}

trait PilotProvider {
	def newPilot(plane: ActorRef): Actor = new Pilot(plane)
	def newCoPilot(plane: ActorRef): Actor = new CoPilot(plane)	
	def newAutoPilot(plane: ActorRef): Actor = new AutoPilot(plane)
}
