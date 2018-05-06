package scala.akka.avionics

import akka.actor.{Actor,ActorLogging,ActorRef}

class AutoPilot(plane: ActorRef) extends Actor with ActorLogging {
    import Plane._
    
    private var controls: ActorRef = context.system.deadLetters
    
	def receive = {
	    case Controls(c) => controls = c
		case _ => println("what should I do")
	}
}
