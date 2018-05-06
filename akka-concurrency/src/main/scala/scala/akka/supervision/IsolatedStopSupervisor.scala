package scala.akka.supervision

import akka.actor.{ActorInitializationException, ActorKilledException}
import scala.concurrent.duration.Duration

abstract class IsolatedStopSupervisor
        (maxRetries: Int = -1, withinTimeRange: Duration = Duration.Inf) 
        extends IsolatedLifecycleSupervisor {
            
	this : SupervisionStrategyFactory =>
	
	import akka.actor.SupervisorStrategy._
	override val supervisorStrategy = makeStrategy(maxRetries, withinTimeRange) {
	    case _ : ActorInitializationException => Stop
	    case _ : ActorKilledException => Stop
	    case _ : Exception => Stop
	    case _ => Escalate
	}	
}
