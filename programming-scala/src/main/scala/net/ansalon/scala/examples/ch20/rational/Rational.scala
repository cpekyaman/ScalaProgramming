package net.ansalon.scala.examples.ch20.rational

trait Rational {
	val numerArg: Int
	val denomArg: Int
	
	val numer = numerArg / g
	val denom = denomArg / g
	
	require(denomArg != 0)
	private val g = gcd(numerArg, denomArg)
	
	private def gcd(a: Int, b: Int): Int =
		if (b == 0) a else gcd(b, a % b)
		
	override def toString = numer +"/"+ denom
}