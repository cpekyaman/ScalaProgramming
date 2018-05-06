package scala.akka.ch08

import akka.actor.{Actor,Terminated}

class MyActor extends Actor {
    def initialize() {
        // actor initialization here
    }
    
    override def preStart() {
        initialize()
        // start child actors here
    }
    
    override def preRestart(reason: Throwable, message: Option[Any]) {
        // default implementation stops children, we don't do it
        postStop()
    }
    
    override def postRestart(reason: Throwable){
        // default implementation calls preStart, we don't call it because it restarts childs
        initialize()
    }
    
    def receive = {
        case Terminated(deadActor) => 
            println("actor dead")
            // child actor can be recreated here
        case _ => 
            println("have a message")
    }
}
