package scala.akka.supervision

import akka.actor.{SupervisorStrategy, OneForOneStrategy, AllForOneStrategy}
import akka.actor.SupervisorStrategy._
import scala.concurrent.duration.Duration

trait SupervisionStrategyFactory {
	def makeStrategy(maxRetries: Int, withinTimeRange: Duration)(decider: Decider): SupervisorStrategy
}

trait OneForOneStrategyFactory extends SupervisionStrategyFactory {
    def makeStrategy(maxRetries: Int, withinTimeRange: Duration)(decider: Decider): SupervisorStrategy =
        OneForOneStrategy(maxRetries, withinTimeRange)(decider)
}

trait AllForOneStrategyFactory extends SupervisionStrategyFactory {
    def makeStrategy(maxRetries: Int, withinTimeRange: Duration)(decider: Decider): SupervisorStrategy =
        AllForOneStrategy(maxRetries, withinTimeRange)(decider)
}
