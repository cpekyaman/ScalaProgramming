package scala.akka.ch07

import akka.actor.{ActorSystem, Props}
import akka.util.Timeout

import scala.akka.avionics._
import scala.concurrent.duration._

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
    system.scheduler.scheduleOnce(3.seconds) {
      system.terminate()
    }
  }
}
