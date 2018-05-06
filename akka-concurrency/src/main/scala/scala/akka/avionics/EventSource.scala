package scala.akka.avionics

import akka.actor.{Actor, ActorRef}

trait EventSource {
    def sendEvent[T](event: T): Unit
    def eventSourceReceive: Actor.Receive
}

object EventSource {
    case class Register(listener: ActorRef)
    case class Unregister(listener: ActorRef)
}

trait ActorEventSource extends EventSource {
    import EventSource._
    
    // We're going to use a Vector but many structures would be adequate
    private[avionics] var listeners = Vector.empty[ActorRef]
    
    // Sends the event to all of our listeners
    override def sendEvent[T](event: T): Unit = listeners foreach { _ ! event }
    
    // We create a specific partial function to handle the
    // messages for our event listener. Anything that mixes in
    // our trait will need to compose this receiver
    override def eventSourceReceive: Actor.Receive = {
        case Register(listener) =>
            listeners = listeners :+ listener
        case Unregister(listener) =>
            listeners = listeners filter { _ != listener }
    }
}
