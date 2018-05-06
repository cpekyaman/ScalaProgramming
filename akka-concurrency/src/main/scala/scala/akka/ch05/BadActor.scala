package scala.akka.ch05

import akka.actor.{Actor, Props, ActorSystem}

class BadActor extends Actor {
    def receive = {
        case "Good Morning" => println("Good Morning too !!!")
        case "You're terrible" => println("You suck !!!")
        case _ => println("What ???")
    }
}

object BadActorMain {
    val system = ActorSystem("investigation")
    val actor = system.actorOf(Props[BadActor], "Bad")
    
    def send(msg: String) {
        println(msg)
        actor ! msg
        Thread.sleep(100)
    }
    
    def main(args: Array[String]) {
        send("Good Morning")
        send("You're terrible")
        send("Hello")
        system.terminate()
    }
}
