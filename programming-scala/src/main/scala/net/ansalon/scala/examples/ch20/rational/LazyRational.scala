package net.ansalon.scala.examples.ch20.rational

trait LazyRational {
	val numerArg: Int
	val denomArg: Int
	
	lazy val numer = numerArg / g
	lazy val denom = denomArg / g
	
	private lazy val g = {
		require(denomArg != 0)
		gcd(numerArg, denomArg)
	}
	
	private def gcd(a: Int, b: Int): Int = 
		if (b == 0) a else gcd(b, a % b)
		
	override def toString = numer + "/" + denom
}