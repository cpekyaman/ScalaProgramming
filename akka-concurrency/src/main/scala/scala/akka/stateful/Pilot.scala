package scala.akka.stateful

import akka.actor.{ActorRef, Actor}
import fsm.FlyingProvider

object Pilot {
	import fsm.FlyingBehaviour._
	import Controller._
	
	val tipsyElevatorCalc: Calculator = { (target, status) =>
	    val msg = calcElevator(target, status)
	    msg match {
	        case StickForward(amt) => StickForward(amt * 1.03f)
            case StickBack(amt) => StickBack(amt * 1.03f)
            case m => m
	    }
	}
	
	val tipsyAileronCalc: Calculator = { (target, status) =>
	    val msg = calcAilerons(target, status)
	    msg match {
	        case StickLeft(amt) => StickLeft(amt * 1.03f)
	        case StickRight(amt) => StickRight(amt * 1.03f)
	        case m => m
	    }
	}
	
	val zaphodElevatorCalc: Calculator = { (target, status) =>
	    val msg = calcElevator(target, status)
	    msg match {
	        case StickForward(amt) => StickBack(amt)
            case StickBack(amt) => StickForward(amt)
            case m => m
	    }
	}
	
	val zaphodAileronCalc: Calculator = { (target, status) =>
	    val msg = calcAilerons(target, status)
	    msg match {
	        case StickLeft(amt) => StickRight(amt)
	        case StickRight(amt) => StickLeft(amt)
	        case m => m
	    }
	}
}

class Pilot(plane: ActorRef,
            altimeter: ActorRef,
            heading: ActorRef) extends Actor {
    this: DrinkingProvider with FlyingProvider =>
    
    import Pilot._
    import scala.akka.avionics.Pilot._
    import akka.actor.FSM._
    import fsm.FlyingBehaviour._
    import DrinkingBehaviour._
    
    val copilotName = context.system.settings.config.getString("scala.akka.avionics.crew.copilot")
    
    def setCourse(flyer: ActorRef) {
        flyer ! Fly(CourseTarget(20000,250,System.currentTimeMillis + 30000))
    }
    
    override def preStart() {
        context.actorOf(newDrinkingBehaviour(self), "DrinkingBehaviour")
        context.actorOf(newFlyingBehaviour(plane,altimeter,heading), "FlyingBehaviour")
    }
    
    def bootstrap: Receive = {
        case ReadyToGo =>
            val copilot = context.actorFor("../" + copilotName)
            val flyer = context.actorFor("FlyingBehaviour")
            flyer ! SubscribeTransitionCallBack(self)
            setCourse(flyer)
            context.become(sober(copilot, flyer))
    }
    
    def idle: Receive = {
        case _ => // do nothing
    }
    
    def sober(copilot: ActorRef, flyer: ActorRef): Receive = {
        case FeelingSober => // already sober
        case FeelingTipsy => becomeTipsy(copilot, flyer)
        case FeelingZaphod => becomeZaphod(copilot, flyer)
    }
    
    def tipsy(copilot: ActorRef, flyer: ActorRef): Receive = {
        case FeelingSober => becomeSober(copilot, flyer)
        case FeelingTipsy => // already tipsy
        case FeelingZaphod => becomeZaphod(copilot, flyer)
    }
    
    def zaphod(copilot: ActorRef, flyer: ActorRef): Receive = {
        case FeelingSober => becomeSober(copilot, flyer)
        case FeelingTipsy => becomeTipsy(copilot, flyer)
        case FeelingZaphod => // already zaphod
    }
    
    def becomeSober(copilot: ActorRef, flyer: ActorRef) {
        flyer ! NewElevatorCalc(calcElevator)
        flyer ! NewAileronCalc(calcAilerons)
        context.become(sober(copilot, flyer))
    }
    
    def becomeTipsy(copilot: ActorRef, flyer: ActorRef) {
        flyer ! NewElevatorCalc(tipsyElevatorCalc)
        flyer ! NewAileronCalc(tipsyAileronCalc)
        context.become(tipsy(copilot, flyer))
    }
    
    def becomeZaphod(copilot: ActorRef, flyer: ActorRef) {
        flyer ! NewElevatorCalc(zaphodElevatorCalc)
        flyer ! NewAileronCalc(zaphodAileronCalc)
        context.become(zaphod(copilot, flyer))
    }
    
    override def unhandled(msg: Any): Unit = {
        msg match {
            case Transition(_,_,Flying) => setCourse(sender)
            case Transition(_,_,Idle) => context.become(idle)
            case Transition(_,_,_) =>
            case CurrentState(_,_) =>
            case m => super.unhandled(m)
        }
    }
    
    def receive = bootstrap
}
