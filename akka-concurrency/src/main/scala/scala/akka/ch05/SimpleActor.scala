package scala.akka.ch05

import akka.actor.Actor

case class Gamma(s: String)
case class Beta(s: String, g: Gamma)
case class Alpha(b1: Beta, b2: Beta)

class SimpleActor extends Actor {
    def receive = {
        case "Hello" => println("Hi")
        case s: String => println(s"string : $s")
        case Alpha(Beta(b1, Gamma(g1)), Beta(b2, Gamma(g2))) 
            => println(s"beta1: $b1, beta2: $b2, gamma1: $g1, gamma2: $g2")
        case _ => println("What ???")
    }
}
