package net.ansalon.scala.examples.ch08

import scala.io.Source

object LongLines {
	def processFiles(filename: String, width: Int) {
		var source = Source.fromFile(filename);
		for(line <- source.getLines)
			processLine(filename, width, line)
	}
	
	def processLine(filename: String, width: Int, line: String) {
		if(line.length > width)
			println(filename + ": " + line.trim)
	}
}