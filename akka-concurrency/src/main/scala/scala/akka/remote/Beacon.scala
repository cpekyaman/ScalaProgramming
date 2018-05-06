package scala.akka.remote

import akka.actor.Actor
import scala.concurrent.duration._

trait BeaconResolution {
	lazy val beaconInterval = 1.second
}

trait BeaconProvider {
	def newBeacon(heading: Float) = Beacon(heading)
}

object Beacon {
	case class BeaconHeading(heading: Float)
	
	def apply(heading: Float) = new Beacon(heading) with BeaconResolution
}

class Beacon(heading: Float) extends Actor {
    this: BeaconResolution =>
    
    import Beacon._
    import scala.akka.avionics.EventSource._
    import scala.akka.patterns.bus.EventBusForActors
    import EventBusForActors._
    
    implicit val ec = context.dispatcher
    
    val eventBus = new EventBusForActors[BeaconHeading, Boolean]({
        _: BeaconHeading => true
    })

    case object Tick
    
    val ticker = context.system.scheduler.schedule(beaconInterval, beaconInterval, self, Tick)
    
    def receive = {
        case Register(listener) => eventBus.subscribe(listener, true)
        case Unregister(listener) => eventBus.unsubscribe(listener)
        case Tick => eventBus.publish(BeaconHeading(heading))
    }
}
