package net.ansalon.scala.examples.ch09

import Curry._

object Main {

  def main(args: Array[String]): Unit = { 
	  val onePlus = curriedSum(1)_
	  
	  println(onePlus(3))
	   
	  withPrintWriter(new java.io.File("cenk.txt")) {
	 	  _.println("Cenk was here")
	  }
	   
	   val cont = withPrintWriter(new java.io.File("cenk2.txt"))_	   
	   cont { _.println("Raistlin was here") }
  }
}