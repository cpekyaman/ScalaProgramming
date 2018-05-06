package scala.akka.supervision.avionics.provider

import akka.actor.Actor
import scala.akka.avionics.Altimeter

trait AltimeterProvider {
	def newAltimeter: Actor = Altimeter()
}
