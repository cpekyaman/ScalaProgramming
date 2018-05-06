package net.ansalon.scala.examples.ch08

object FunctionValues {
	def test() {
		val printer = (x: Int) => println(x)
		val numbers = List(-11,-10,-5,0, 5, 10)
		
		numbers.foreach( printer ) 
		
		val pFilter = (x: Int) => x > 0
		val positives = numbers.filter( pFilter )
		positives.foreach( printer ) 
	}
}