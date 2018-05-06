package scala.akka.supervision.avionics.provider

import akka.actor.Actor
import scala.akka.avionics.LeadFlightAttendant

trait LeadFlightAttendantProvider {
	def newLeadAttendant: Actor = LeadFlightAttendant()
}
