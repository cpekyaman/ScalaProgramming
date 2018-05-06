package net.ansalon.scala.examples.ch10

private class LineElement(content: String) extends Element{
	override val contents = Array(content)
	override val height: Int = 1
	override val width: Int = content.length
}