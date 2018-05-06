package scala.akka.agents

import akka.actor.{ActorRef, Actor, FSM}
import akka.agent.Agent
import scala.collection.immutable.Queue
import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit

object Bathroom {
    sealed trait State
    case object Vacant extends State
    case object Occupied extends State
    
    sealed trait Data
    case object NotInUse extends Data
    case class InUse(by: ActorRef, atTime: Long, queue: Queue[ActorRef]) extends Data
    
    case object WannaUseBathroom
    case object BathroomAvailable
    case class Finished(gender: Gender)
    
    private def updateCounter(male: Agent[GenderAndTime], 
                              female: Agent[GenderAndTime], 
                              gender: Gender, duration: Duration) {
        
        gender match {
            case Male => male send { c => GenderAndTime(Male, duration.max(c.peakDuration), c.count + 1) }
            case Female => female send { c => GenderAndTime(Female, duration.max(c.peakDuration), c.count + 1) }
        }                              
    }
}

class Bathroom(femaleCounter: Agent[GenderAndTime], maleCounter: Agent[GenderAndTime])
        extends Actor 
        with FSM[Bathroom.State, Bathroom.Data] {
        
    import Bathroom._
    
    startWith(Vacant, NotInUse)
    
    when(Vacant) {
        case Event(WannaUseBathroom, _) => 
            sender ! BathroomAvailable
            goto(Occupied) using InUse(sender, System.currentTimeMillis, Queue())
    }
    
    when(Occupied) {
        case Event(WannaUseBathroom, data: InUse) => 
            stay using data.copy(queue = data.queue.enqueue(sender))
        case Event(Finished(gender), data: InUse) if sender == data.by =>
            updateCounter(maleCounter, femaleCounter, gender, Duration(System.currentTimeMillis - data.atTime, TimeUnit.MILLISECONDS))
            if(data.queue.isEmpty) {
                goto(Vacant) using NotInUse
            } else {
                val (next, q) = data.queue.dequeue
                next ! BathroomAvailable
                stay using InUse(next, System.currentTimeMillis, q)
            }
            
    }
    
    initialize
}
