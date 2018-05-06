package net.ansalon.scala.examples.ch10

private class UniformElement (
	ch: Char,
	override val width: Int,
	override val height: Int
) extends Element{
	private val line = ch.toString * width
	override def contents = Array.fill(height)(line)
}