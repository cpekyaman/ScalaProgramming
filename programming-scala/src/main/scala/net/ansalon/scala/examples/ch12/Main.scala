package net.ansalon.scala.examples.ch12

object Main {
  def main(args: Array[String]): Unit = { 
    println("Using Queue")
    val queue = new BasicIntQueue    
    queue.put(5)
    queue.put(8)
    println(queue.get())
    println(queue.get())
    
    println("Using DoublingQueue")
    val queue2 = new DoublingQueue
    queue2.put(5)
    queue2.put(8)
    println(queue2.get())
    println(queue2.get())
    
    val stackable = (new BasicIntQueue with Incrementing with Filtering)
    stackable.put(-1)
    stackable.put(0)
    stackable.put(1)
    println(stackable.get())
  }
}