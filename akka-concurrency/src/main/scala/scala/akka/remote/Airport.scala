package scala.akka.remote

import akka.actor.{Actor, ActorRef, Props}

trait AirportSpecifics {
    lazy val headingTo: Float = 0.0f
    lazy val altitude: Float = 0
}

object Airport {
    case class DirectFlyer(flyer: ActorRef)
    case class StopFlyer(flyer: ActorRef)
    
    def toronto(): Props = Props(new Airport with BeaconProvider 
                                             with AirportSpecifics {
        override lazy val headingTo: Float = 314.3f
        override lazy val altitude: Float = 26000
    })
}

class Airport extends Actor {
    this: AirportSpecifics with BeaconProvider =>
    
    import Airport._
    import Beacon._
    import scala.akka.stateful.fsm.FlyingBehaviour._
    
    val beacon = context.actorOf(Props(newBeacon(headingTo)), "Beacon")
    
    def receive = {
        // FlyingBehaviour instances subscribe to this Airport in
        // order to be told where they should be flying
        case DirectFlyer(flyer) =>
            val oneHourFromNow = System.currentTimeMillis + 60 * 60 * 1000
            val when = oneHourFromNow
            // But, we can't let them get BeaconHeading messages, since
            // those are not understood by FlyingBehaviour instances.
            // We need to transform those messages into appropriate
            // 'Fly' messages
            context.actorOf(Props(new MessageTransformer(from = beacon, to = flyer, {
                case BeaconHeading(heading) => Fly(CourseTarget(altitude, heading, when))
            })))
        case StopFlyer(flyer) => context.children.foreach { context.stop }
    }
}
