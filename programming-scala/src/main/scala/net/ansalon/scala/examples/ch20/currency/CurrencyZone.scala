package net.ansalon.scala.examples.ch20.currency

abstract class CurrencyZone {
	type Currency <: AbstractCurrency
	val CurrencyUnit: Currency
	def make(x: Long): Currency
	
	abstract class AbstractCurrency {
		val amount: Long
		val code: String
				
		def + (that: Currency): Currency =
			make(this.amount + that.amount)
		def * (x: Double): Currency =
			make((this.amount * x).toLong)
		def - (that: Currency): Currency =
			make(this.amount - that.amount)
		def / (that: Double) =
			make((this.amount / that).toLong)
		def / (that: Currency) =
			this.amount.toDouble / that.amount
		
		def from(other: CurrencyZone#AbstractCurrency): Currency = {
		  val rate = Converter.exchangeRate(other.code)(this.code)
		  make(math.round(other.amount.toDouble * rate))
		}			
		
		private def decimals(n: Long): Int =
			if (n == 1) 0 else 1 + decimals(n / 10)
			
		override def toString = {
		  val value = amount.toDouble / CurrencyUnit.amount.toDouble
		  val formatStr = "%." + decimals(CurrencyUnit.amount) + "f" 
		  value formatted(formatStr) + " " + code
		}			
	}
}