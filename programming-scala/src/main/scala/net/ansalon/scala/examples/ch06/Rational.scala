package net.ansalon.scala.examples.ch06

class Rational(n:Int, d:Int) {
	require(d != 0)
	
	private var g = gcd(n.abs, d.abs)	
	val num: Int = n / g
	val den: Int = d /g
	
	def this(n: Int) = this(n, 1)
	
	override def toString = num + "/" + den
	
	def + (that: Rational): Rational =
		new Rational(num * that.den + that.num * den, den * that.den)
	def + (i: Int): Rational =
		new Rational(num + i * den, den)
	
	def * (that: Rational): Rational =
		new Rational(num * that.num, den * that.den)
	def * (i: Int): Rational =
		new Rational(num * i, den)
	
	def less(that: Rational) =
		num * that.den < that.num * den
	def greater(that: Rational) =
		that.less(this)
	def max(that: Rational) =
		if(less(that)) that else this
		
	private def gcd(a: Int, b: Int): Int =
		if(b == 0) a else gcd(b, a % b)
}