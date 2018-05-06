package scala.akka.stateful.fsm

import akka.actor.{ActorRef, Actor, FSM, Props}

import scala.concurrent.duration._

trait FlyingProvider {
    def newFlyingBehaviour(plane: ActorRef,
                           altimeter: ActorRef,
                           heading: ActorRef): Props =
        Props(new FlyingBehaviour(plane, altimeter, heading))
}

object FlyingBehaviour {
    import scala.akka.stateful.Controller._
    
    sealed trait State
    case object Idle extends State
    case object Flying extends State
    case object PreparingToFly extends State
    
    case class CourseTarget(altitude: Float, heading: Float, byMillis: Long)
    case class CourseStatus(altitude: Float, heading: Float, headingSinceMs: Long, altitudeSinceMs: Long)
    
    case class NewElevatorCalc(calc: Calculator)
    case class NewAileronCalc(calc: Calculator)
    
    type Calculator = (CourseTarget,CourseStatus) => Any
    
    sealed trait Data
    case object Uninitialized extends Data
    case class FlightData(controls: ActorRef, 
                          elevCalc: Calculator, 
                          bankCalc: Calculator, 
                          target: CourseTarget,
                          status: CourseStatus) extends Data
                          
    
    case class Fly(target: CourseTarget)
    
    def currentMs = System.currentTimeMillis
    
    def calcElevator(target: CourseTarget, status: CourseStatus): Any = {
        val altitude = (target.altitude - status.altitude).toFloat
        val duration = (target.byMillis - status.altitudeSinceMs)
        
        if(altitude < 0) StickForward((altitude / duration) * -1) else StickBack(altitude / duration)
    }
    
    def calcAilerons(target: CourseTarget, status: CourseStatus): Any = {
        import scala.math.{abs, signum}
        
        val diff = target.heading - status.heading
        val dur = target.byMillis - status.headingSinceMs
        val amount = if (abs(diff) < 180) diff else signum(diff) * (abs(diff) - 360f)
        
        if (amount > 0) StickRight(amount / dur) else StickLeft((amount / dur) * -1)
    }
}

class FlyingBehaviour(plane: ActorRef, 
                      altimeter: ActorRef, 
                      heading: ActorRef) 
    extends Actor
    with FSM[FlyingBehaviour.State, FlyingBehaviour.Data] {
    
    import FSM._
    import FlyingBehaviour._
    
    import scala.akka.stateful.Plane._
    import scala.akka.stateful.HeadingIndicator._
    
    import scala.akka.avionics.Pilot._
    import scala.akka.avionics.Plane._
    import scala.akka.avionics.EventSource._
    import scala.akka.avionics.Altimeter._
    
    case object Adjust
    
    startWith(Idle, Uninitialized)
    
    when(Idle) {
        case Event(Fly(target), _) =>
            goto(PreparingToFly) using FlightData(context.system.deadLetters,
                                                  calcElevator,
                                                  calcAilerons,
                                                  target,
                                                  CourseStatus(-1,-1,0,0))
    }
    
    when(PreparingToFly)(transform {
        case Event(HeadingUpdate(head), d: FlightData) => 
            stay using d.copy(status = d.status.copy(heading = head, headingSinceMs = currentMs))
        case Event(AltitudeChange(alt), d: FlightData) =>
            stay using d.copy(status = d.status.copy(altitude = alt, altitudeSinceMs = currentMs))
        case Event(Controls(controller), d: FlightData) =>
            stay using d.copy(controls = controller)
        case Event(LostControl, _) => 
            plane ! LostControl
            goto(Idle)
    } using {
        case s if prepComplete(s.stateData) => s.copy(stateName = Flying)    
    })
    
    def prepComplete(data: Data): Boolean = {
        data match {
            case FlightData(c,_,_,_,s) =>
                if(!c.isTerminated && s.heading != -1f && s.altitude != -1f) true else false
            case _ => false
        }
    }
    
    when(Flying) {
        case Event(HeadingUpdate(head), d: FlightData) => 
            stay using d.copy(status = d.status.copy(heading = head, headingSinceMs = currentMs))
        case Event(AltitudeChange(alt), d: FlightData) =>
            stay using d.copy(status = d.status.copy(altitude = alt, altitudeSinceMs = currentMs))
        case Event(Adjust, data: FlightData) => stay using adjust(data)
        case Event(NewElevatorCalc(calc), d: FlightData) => stay using d.copy(elevCalc = calc)
        case Event(NewAileronCalc(calc), d: FlightData) => stay using d.copy(bankCalc = calc)
    }
    
    def adjust(data: FlightData): FlightData = {
        val FlightData(c, ec, bc, t, s) = data
        c ! ec(t, s)
        c ! bc(t, s)
        data
    }
    
    whenUnhandled {
        case Event(RelinquishControl, _) => goto(Idle)
    }
    
    onTransition {
        case Idle -> PreparingToFly =>
            plane ! GiveMeControl
            altimeter ! Register(self)
            heading ! Register(self)
            setTimer("AcquireControl", LostControl, 5.seconds, repeat = false)
        case PreparingToFly -> Flying =>
            cancelTimer("AcquireControl")
            setTimer("Adjustment", Adjust, 200.milliseconds, repeat = true)
        case Flying -> _ => cancelTimer("Adjustment")
        case _ -> Idle =>
            heading ! Unregister(self)
            altimeter ! Unregister(self)
    }
    
    initialize
}
                   
                      
