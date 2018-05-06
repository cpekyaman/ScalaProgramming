package net.ansalon.scala.examples.ch20.currency.zone

import net.ansalon.scala.examples.ch20.currency._

object US extends CurrencyZone {
	abstract class Dollar extends AbstractCurrency {
		val code = "USD"
	}
	
	// fix the currency class
	type Currency = Dollar
	// tell how to create new
	def make(cents: Long) = new Dollar {
		val amount = cents
	}
	
	// values representing bases
	val Cent = make(1)
	val Dollar = make(100)
	
	// base unit
	val CurrencyUnit = Dollar
}