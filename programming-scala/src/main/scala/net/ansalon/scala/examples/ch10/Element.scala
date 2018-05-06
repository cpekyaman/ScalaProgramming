package net.ansalon.scala.examples.ch10

object Element {
	def elem(contents: Array[String]): Element 
		= new ArrayElement(contents)
	def elem(chr: Char, width: Int, height: Int): Element 
		= new UniformElement(chr, width, height)
	def elem(line: String): Element
		= new LineElement(line)
}

import Element.elem

abstract class Element {
	def contents : Array[String]
	def width: Int = if (height == 0) 0 else contents(0).length
	def height: Int = contents.length
	
	override def toString = contents mkString "\n"
	
	def above(that: Element): Element = {
		val this1 = this widen that.width
		val that1 = that widen this.width
		
		new ArrayElement(this1.contents ++ that1.contents)
	}		
	
	def beside(that: Element): Element = {
		val this1 = this heighten that.height
		val that1 = that heighten this.height

		new ArrayElement(
			for( 
				(line1, line2) <- this1.contents zip that1.contents
			) yield line1 + line2
		)
	}
	
	def widen(w: Int): Element =
		if (w <= width) this
		else {
			val left = elem(' ', (w - width) / 2, height)
			var right = elem(' ', w - width - left.width, height)
			left beside this beside right
		}	
	
	def heighten(h: Int): Element =
		if (h <= height) this
		else {
			val top = elem(' ', width, (h - height) / 2)
			var bot = elem(' ', width, h - height - top.height)
			top above this above bot
		}	
}