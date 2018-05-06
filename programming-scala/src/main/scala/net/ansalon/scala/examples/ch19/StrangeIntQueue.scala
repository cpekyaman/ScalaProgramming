package net.ansalon.scala.examples.ch19

class StrangeIntQueue (
	leading: List[Int]
  ) extends Queue[Int](leading, Nil) {
  
   def this() = this(Nil)
   
   def enqueue(x: Int): Queue[Int] = {
	  println(math.sqrt(x))
	  super.enqueue(x)
   }
}