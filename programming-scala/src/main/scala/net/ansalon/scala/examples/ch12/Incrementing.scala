package net.ansalon.scala.examples.ch12

trait Incrementing extends IntQueue {
	abstract override def put(x: Int) { super.put(x + 1) }
}