package scala.akka.io

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import akka.io.{IO, Tcp}
import akka.util.{ByteString, Timeout}

import scala.akka.io.TelnetServer.ascii
import scala.akka.routing.Plane
import scala.concurrent.duration._

case class NewMessage(msg: String)

object TelnetServer {
  val welcome =
    """|Welcome to the Airplane!
       |----------------|
       |Valid commands are: 'heading' and 'altitude'
       |
       |> """.stripMargin

  implicit val askTimeout = Timeout(1.second)

  def ascii(bytes: ByteString): String = bytes.decodeString("UTF-8").trim
}

class TcpHandler extends Actor {
  // Helpers just to make it easier to format for the book :)
  def headStr(head: Float): ByteString =
    ByteString(f"current heading is: $head%3.2f degrees\n\n> ")

  def altStr(alt: Double): ByteString =
    ByteString(f"current altitude is: $alt%5.2f feet\n\n> ")

  def unknown(str: String): ByteString =
    ByteString(f"current $str is: unknown\n\n> ")

  import Tcp._

  def receive = {
    case Received(data) ⇒
      val cmd = ascii(data)
      cmd match {
        case "shutdown" => context stop self
        case "s" => context stop self
        case "heading" => sender() ! Write(headStr(10.5f))
        case "altitude" => sender() ! Write(altStr(10.5f))
        case _ => sender() ! Write(ByteString("What ???\r\n"))
      }

    case PeerClosed ⇒ context stop self
  }
}

class TelnetServer(plane: ActorRef) extends Actor with ActorLogging {

  import Tcp._
  import context.system

  IO(Tcp) ! Bind(self, new InetSocketAddress("0.0.0.0", 2000))

  def receive = {
    case b@Bound(address) =>
      log.info("Telnet Server listeninig on port {}", address)
      context.parent ! b
    case Connected(remote, local) =>
      log.info("new incoming connection")
      val handler = context.actorOf(Props[TcpHandler])
      val connection = sender()
      connection ! Register(handler)
  }
}

object ServerMain {
  val system = ActorSystem.create("TelnetServerSimulation")

  def main(args: Array[String]) {
    val plane = system.actorOf(Props(Plane()), "Plane")
    val server = system.actorOf(Props(new TelnetServer(plane)), "Telnet")
  }
}
