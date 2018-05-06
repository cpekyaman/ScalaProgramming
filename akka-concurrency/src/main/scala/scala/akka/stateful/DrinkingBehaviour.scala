package scala.akka.stateful

import akka.actor.{ActorRef, Actor, Props}
import scala.concurrent.duration._

trait DrinkingProvider {
    def newDrinkingBehaviour(drinker: ActorRef): Props = Props(DrinkingBehaviour(drinker))
}

object DrinkingBehaviour {
	case class LevelChanged(level: Float)
	
	case object FeelingSober
	case object FeelingTipsy
	case object FeelingZaphod
	
	def apply(drinker: ActorRef) = new DrinkingBehaviour(drinker) with DrinkingResolution
}

trait DrinkingResolution {
	import scala.util.Random
	
	def initialSobering: FiniteDuration = 1.seconds
	def soberingInterval: FiniteDuration = 1.seconds
	def drinkInterval: FiniteDuration = Random.nextInt(300).seconds
}

class DrinkingBehaviour(drinker: ActorRef) extends Actor {
    this: DrinkingResolution =>
    import DrinkingBehaviour._
    
    var currentLevel = 0f
    
    implicit val ec = context.dispatcher
    val scheduler = context.system.scheduler
    
    val sobering = scheduler.schedule(initialSobering, soberingInterval, self, LevelChanged(-0.0001f))
    
    override def postStop() {
        sobering.cancel()        
    }
    
    override def preStart() {
        drink()
    }
    
    def drink() = scheduler.scheduleOnce(drinkInterval, self, LevelChanged(0.005f))
    
    def receive = {
        case LevelChanged(amount) =>
            currentLevel = (currentLevel + amount).max(0f)
            
            drinker ! (
                if(currentLevel <= 0.01f) {
                    drink()
                    FeelingSober
                } else if(currentLevel <= 0.03f) {
                    drink()
                    FeelingTipsy
                } else {
                    FeelingZaphod
                }
            )
    }
}


