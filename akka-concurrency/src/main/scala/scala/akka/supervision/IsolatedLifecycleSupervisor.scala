package scala.akka.supervision

import akka.actor.{Actor}

object IsolatedLifecycleSupervisor {
    case object WaitForStart
    case object Started	
}

trait IsolatedLifecycleSupervisor extends Actor {
    import IsolatedLifecycleSupervisor._
    
    def receive = {
        case WaitForStart =>
            sender ! Started
        case m =>
            throw new Exception(s"do not call ${self.path.name} directly ${m}")
    }
    
    def childStarter(): Unit
    
    final override def preStart() { childStarter() }
    
    final override def postRestart(reason: Throwable) {}
    
    // default implementation stops children, we don't do it
    final override def preRestart(reason: Throwable, message: Option[Any]){}
}
