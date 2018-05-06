package net.ansalon.scala.examples.ch12

trait Doubling extends IntQueue {
	abstract override def put(x: Int) { super.put(2 * x) }
}