package net.ansalon.scala.examples.ch20

class Concrete extends Abstract {
	type T = String
	def transform(x: T): T = { x + x }
	val initial = "hello"
	var current = initial
}