package scala.akka.routing

import akka.actor.{ActorRef, Actor, OneForOneStrategy, ActorKilledException, ActorInitializationException, Props}
import akka.routing.BroadcastGroup

object PassengerSupervisor {
	case object GetPassengerBroadcaster
	case class PassengerBroadcaster(broadcaster: ActorRef)
	
	def apply(callButton: ActorRef, bathrooms: ActorRef) = 
	    new PassengerSupervisor(callButton, bathrooms) with PassengerProvider    
}

class PassengerSupervisor(callButton: ActorRef, bathrooms: ActorRef) extends Actor {
    this: PassengerProvider =>
    
    import scala.akka.util.ActorUtil.actorFor
    import PassengerSupervisor._
    import akka.actor.SupervisorStrategy
    import SupervisorStrategy._
    import com.typesafe.config.ConfigList
    import scala.collection.JavaConverters._

    // We'll resume our immediate children instead of
    // restarting them on an Exception
    override val supervisorStrategy = newStrategy(Resume)
    
    // Actor and its subordinate supervisor
    case class GetChildren(forSomeone: ActorRef)
    case class Children(children: scala.collection.immutable.Iterable[ActorRef], childrenFor: ActorRef)
     
    override def preStart() {
        context.actorOf(Props(childSupervisor()), "PassengersSupervisor")
    }
    
    private def childSupervisor(): Actor = {
        
        new Actor {
            val config = context.system.settings.config
            override val supervisorStrategy = newStrategy(Stop)
            
            override def preStart() { 
                // Get our passenger names from the configuration
                val passengers = config.getList("scala.akka.avionics.passengers")                
                passengers.asInstanceOf[ConfigList].unwrapped().asScala.foreach { nameWithSeat =>                    
                    // Convert spaces to underscores to comply with URI standard
                    context.actorOf(Props(newPassenger(callButton, bathrooms)), passengerId(nameWithSeat))
                }
            }
            
            def receive = {
                case GetChildren(forSomeone) => sender ! Children(context.children, forSomeone)
            }
        }
    }
        
    private def passengerId(nameWithSeat: Any): String = 
        nameWithSeat.asInstanceOf[ConfigList].unwrapped().asScala.mkString("-").replaceAllLiterally(" ", "_")
    
    private def newStrategy(fallbackDirective: Directive): SupervisorStrategy = OneForOneStrategy() {
        case _: ActorKilledException => Escalate
        case _: ActorInitializationException => Escalate
        case _ => fallbackDirective
    }
    
    def noRouter: Receive = {
        case GetPassengerBroadcaster =>
            val passengers = actorFor(context, "PassengersSupervisor")
            passengers ! GetChildren(sender)
        case Children(passengers, destinedFor) =>
            val router = context.actorOf(Props().withRouter(BroadcastGroup(passengers.map(_.path.toStringWithoutAddress))), "Passengers")
            destinedFor ! PassengerBroadcaster(router)
            context.become(withRouter(router))
    }
    
    def withRouter(router: ActorRef): Receive = {
        case GetPassengerBroadcaster =>
            sender ! PassengerBroadcaster(router)
        case Children(_, destinedFor) =>
            destinedFor ! PassengerBroadcaster(router)
    }
    
    def receive = noRouter
}
