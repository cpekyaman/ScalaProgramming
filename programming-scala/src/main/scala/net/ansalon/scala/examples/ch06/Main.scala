package net.ansalon.scala.examples.ch06

object Main {
	def main(args: Array[String]) {
		val rat1 = new Rational(1,2)
		println(rat1)
		
		val rat2 = new Rational(3,7)
		println(rat2)
		
		println(rat1 + rat2)
		
		val rat3 = new Rational(12, 8)
		println(rat3)
		
		val rat4 = new Rational(8, 12)
		println(rat4)
		
		println(rat3 * rat4)
	}
}