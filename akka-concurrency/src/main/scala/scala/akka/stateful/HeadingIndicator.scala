package scala.akka.stateful

import scala.akka.avionics.{EventSource, ActorEventSource}
import akka.actor.{Actor,ActorLogging}
import scala.concurrent.duration._

object HeadingIndicator {
	case class BankChange(amount: Float)
	case class HeadingUpdate(amount: Float)
	
	def apply() = new HeadingIndicator with ActorEventSource
}

class HeadingIndicator extends Actor with ActorLogging {
	this: EventSource => 
	
	import HeadingIndicator._
	import context._
	
	case object Tick
	
	private val maxDegreesPerSec: Int = 5
	
	private val ticker = system.scheduler.schedule(100.millis,100.millis,self,Tick)
	
	private var lastTick = System.currentTimeMillis
	
	// The current rate of our bank
    private var rateOfBank = 0f
    // Holds our current direction
    private var heading = 0f
    
    def selfReceive: Receive = {
        // Keeps the rate of change within [-1, 1]
        case BankChange(amount) =>
            rateOfBank = amount.min(1.0f).max(-1.0f)
        // Calculates our heading delta based on the current
        // rate of change, the time delta from our last
        // calculation, and the max degrees per second
        case Tick =>
            val tick = System.currentTimeMillis
            val timeDelta = (tick - lastTick) / 1000f
            val degs = rateOfBank * maxDegreesPerSec
            heading = (heading + (360 + (timeDelta * degs))) % 360
            lastTick = tick
            
            sendEvent(HeadingUpdate(heading))
    }
    
    def receive = eventSourceReceive orElse selfReceive
    
    override def postStop(): Unit = ticker.cancel
}
