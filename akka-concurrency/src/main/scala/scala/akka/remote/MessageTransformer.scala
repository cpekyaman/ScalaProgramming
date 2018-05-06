package scala.akka.remote

import akka.actor.{Actor, ActorRef}

class MessageTransformer(from: ActorRef, to: ActorRef, transformer: PartialFunction[Any, Any]) extends Actor {
    import scala.akka.avionics.EventSource._
    
    override def preStart() {
        from ! Register(self)
    }
    
    override def postStop() {
        from ! Unregister(self)
    }
    
    def receive = {
        case m => to forward transformer(m)
    }
}
