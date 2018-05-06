package net.ansalon.scala.examples.ch15

import exp._

object Express extends App {
	val f = new ExprFormatter
	
	val e1 = Binary("*", 
	    Binary("/", Number(1), Number(2)), 
	    Binary("+", Var("x"), Number(1)))
	val e2 = Binary("+", 
	    Binary("/", Var("x"), Number(2)), 
	    Binary("/", Number(1.5), Var("x")))
	val e3 = Binary("/", e1, e2)
	
	def show(e: Expr) = println(f.format(e)+ "\n\n")
	
	for (e <- Array(e1, e2, e3)) show(e)
}