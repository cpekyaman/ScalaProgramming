package scala.akka.future

import akka.actor.{ActorSystem,Props, Actor}
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.{Future,Await}

class EchoActor extends Actor {
    def receive = {
        case m: String => sender ! s"Echoing $m"
    }
}

object FutureWithActorApp {
    def main(args: Array[String]) {
        val system = ActorSystem("FutureActor") 
        try {
            implicit val askTimeout = Timeout(1.second)                   
            val a = system.actorOf(Props[EchoActor])   
            val askF = a ? "Hello"
            val result = Await.result(askF, 1.second)
            println(s"Echo result is $result")
        } finally {
            system.terminate()
        }
    }
}
