package scala.akka.avionics

// Imports to help us create Actors, plus logging
import akka.actor.{Actor, ActorLogging}

// The duration package object extends Ints with some
// timing functionality
import scala.concurrent.duration._

object Altimeter {
    def apply() = new Altimeter with ActorEventSource
    
    // Sent to the Altimeter to inform it about rate-of-climb changes
    case class RateChange(amount: Float)
    case class AltitudeChange(altitude: Float)
}  

class Altimeter extends Actor with ActorLogging { this: EventSource =>
    import Altimeter._
    
    // We need an "ExecutionContext" for the scheduler. This
    // Actor's dispatcher can serve that purpose. The
    // scheduler's work will be dispatched on this Actor's own
    // dispatcher
    implicit val ec = context.dispatcher
    
    // The maximum ceiling of our plane in 'feet'
    private val ceiling = 43000
    
    // The maximum rate of climb for our plane in 'feet per minute'
    private[avionics] val maxRateOfClimb = 5000
    
    // The varying rate of climb depending on the movement of the stick
    private[avionics] var rateOfClimb = 0.0f
    
    // current altitude
    private var altitude = 0.0f
    
    // As time passes, we need to change the altitude based on
    // the time passed. The lastTick allows us to figure out
    // how much time has passed
    private var lastTick = System.currentTimeMillis
    
    case object Tick
    private val ticker = context.system.scheduler.schedule(100.millis, 100.millis, self, Tick)
    
    private def selfReceive: Receive = {
        case RateChange(amount) => 
            rateOfClimb = amount.min(1.0f).max(-1.0f) * maxRateOfClimb
            log info(s"Altimeter changed rate of climb to $rateOfClimb.")
        case Tick => 
            val tick = System.currentTimeMillis
            altitude = altitude + ((tick - lastTick) / 60000.0f) * rateOfClimb
            lastTick = tick
            sendEvent(AltitudeChange(altitude))
    }
    
    def receive = eventSourceReceive orElse selfReceive
    
    override def postStop(): Unit = ticker.cancel
}
