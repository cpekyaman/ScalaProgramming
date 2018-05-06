package net.ansalon.scala.examples.ch09

import java.io._

object Curry {
	def curriedSum(x: Int)(y: Int) = x + y
	
	def twice(op: Double => Double, x: Double) = op(op(x))
	
	def withPrintWriter(file: File)( op: PrintWriter => Unit) {
		val writer = new PrintWriter(file)
		try {
			op(writer)
		} finally {
			writer.close()
		}
	}
}