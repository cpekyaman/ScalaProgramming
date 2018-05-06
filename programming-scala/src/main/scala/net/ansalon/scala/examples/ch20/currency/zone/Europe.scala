package net.ansalon.scala.examples.ch20.currency.zone

import net.ansalon.scala.examples.ch20.currency._

object Europe extends CurrencyZone {
  abstract class Euro extends AbstractCurrency {
    val code = "EUR"
  }
  
  type Currency = Euro
  def make(cents: Long) = new Euro {
    val amount = cents
  }
  
  val Cent = make(1)
  val Euro = make(100)
  
  val CurrencyUnit = Euro
}