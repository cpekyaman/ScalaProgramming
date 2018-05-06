package net.ansalon.scala.examples.ch20.currency.zone

import net.ansalon.scala.examples.ch20.currency._

object Japan extends CurrencyZone {
  abstract class Yen extends AbstractCurrency {
    val code = "JPY"
  }
  
  type Currency = Yen
  def make(yen: Long) = new Yen {
    val amount = yen
  }
  
  val Yen = make(1)
  
  val CurrencyUnit = Yen
}