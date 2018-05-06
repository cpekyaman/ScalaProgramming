package scala.akka.avionics

import akka.actor.{Actor, ActorRef}
import akka.util.Timeout

import scala.concurrent.Await

object Pilot {
	case object ReadyToGo
	case object RelinquishControl
}

//TODO: check actorSelection usage
class Pilot(plane: ActorRef) extends Actor {
    import scala.concurrent.duration._
    implicit val actorSelectTimeout = Timeout(1.second)

    var controls: ActorRef = context.system.deadLetters
    var copilot: ActorRef = context.system.deadLetters
    var autopilot: ActorRef = context.system.deadLetters
    
    val copilotName = context.system.settings.config.getString("scala.akka.avionics.crew.copilot")
    
    import Pilot._
    import Plane._
    
    def receive = {
        case ReadyToGo =>
            context.parent ! GiveMeControl
            copilot = Await.result(context.actorSelection("../" + copilotName).resolveOne(), 2.seconds)
            autopilot = Await.result(context.actorSelection("../AutoPilot").resolveOne(), 2.seconds)
        case Controls(c) => controls = c
    }
}

class CoPilot(plane: ActorRef) extends Actor {
    import scala.concurrent.duration._
    implicit val actorSelectTimeout = Timeout(1.second)

    var controls: ActorRef = context.system.deadLetters
    var pilot: ActorRef = context.system.deadLetters
    var autopilot: ActorRef = context.system.deadLetters
    
    val pilotName = context.system.settings.config.getString("scala.akka.avionics.crew.pilot")
    
    import Pilot._
    import Plane._
    import akka.actor.Terminated

    def receive = {
        case ReadyToGo =>
            pilot = Await.result(context.actorSelection("../" + pilotName).resolveOne(), 1.seconds)
            autopilot = Await.result(context.actorSelection("../AutoPilot").resolveOne(), 1.seconds)
            // deathwatch for the pilot
            context.watch(pilot)
        case Controls(c) => controls = c
        case Terminated(_) =>
            // Pilot died, assume control
            plane ! GiveMeControl
    }
}
