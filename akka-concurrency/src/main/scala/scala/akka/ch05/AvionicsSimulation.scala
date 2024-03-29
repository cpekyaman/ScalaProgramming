package scala.akka.ch05

import akka.actor.{Props, Actor, ActorRef, ActorSystem}
import akka.pattern.ask
import scala.concurrent.Await
import akka.util.Timeout
import scala.concurrent.duration._
import scala.akka.avionics._

// The futures created by the ask syntax need an
// execution context on which to run, and we will use the
// default global instance for that context
import scala.concurrent.ExecutionContext.Implicits.global

object AvionicsSimulation {
    // needed for '?' below
    implicit val timeout = Timeout(5.seconds)
    
    private val system = ActorSystem("AvionicsSimulation")
    private val plane = system.actorOf(Props[Plane], "Plane")
    
    def main(args: Array[String]) {
        val control = Await.result((plane ? Plane.GiveMeControl).mapTo[ActorRef], 5.seconds)
        
        system.scheduler.scheduleOnce(200.millis) {
            control ! Controller.StickBack(1.0f)
        }
        
        system.scheduler.scheduleOnce(1.seconds) {
            control ! Controller.StickBack(0.0f)
        }
        
        system.scheduler.scheduleOnce(3.seconds) {
            control ! Controller.StickBack(0.5f)
        }
        
        system.scheduler.scheduleOnce(4.seconds) {
            control ! Controller.StickForward(0.7f)
        }
        
        system.scheduler.scheduleOnce(5.seconds) {
            control ! Controller.StickBack(0.0f)
        }
        
        system.scheduler.scheduleOnce(6.seconds) {
            system.terminate()
        }
    }
}
