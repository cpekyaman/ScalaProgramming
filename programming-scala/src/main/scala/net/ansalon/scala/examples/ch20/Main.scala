package net.ansalon.scala.examples.ch20

import animal._

object Main {
  def main(args: Array[String]): Unit = {  
    val conc = new Concrete
    println(conc transform "cenk")
    println(conc.initial)
    
    var animal: Animal = new Cow
    var food: Food = new Fish
    // below does not compile
    //animal eat food    
    
    for (d <- Direction.values) print(d + " ")
  }
  
  //def using[T, S](obj: T)(operation: T => S) = { -> this gives type error
  // use structural subtype
  def using[T <: { def close(): Unit }, S](obj: T)(operation: T => S) = {
	val result = operation(obj)
	obj.close() 
	result
  }
}