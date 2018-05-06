package scala.akka.future

import akka.actor.Actor
import Actor.Receive

object StatusReporter {
	case object ReportStatus
	
	sealed trait Status
	case object StatusOK extends Status
	case object StatusNotGreat extends Status
	case object StatusBad extends Status
}

trait StatusReporter {
	this: Actor =>
	
	import StatusReporter._
	
	def currentStatus: Status
	
	def statusReceive: Receive = {
		case ReportStatus => sender ! currentStatus
	}
}
