package net.ansalon.scala.examples.ch19

/*
trait Queue[+T] {
	def head: T
	def tail: Queue[T]
	def enqueue(x: T): Queue[T]
}

object Queue {
	def apply[T](xs: T*): Queue[T] = new QueueImpl[T](xs.toList, Nil)
	
	class QueueImpl[T] (
		private val leading: List[T],
	  	private val trailing: List[T]
	  ) extends Queue[T] {
	  private def mirror =
	    if (leading.isEmpty)
	      new QueueImpl(trailing.reverse, Nil)
	    else
	      this
	      
	  def head = mirror.leading.head
	  
	  def tail : QueueImpl[T] = {
	    val q = mirror
	    new QueueImpl(q.leading.tail, q.trailing)
	  }
	  
	  def enqueue(x: T) =
	    new QueueImpl(leading, x :: trailing)
	}
}
*/


object Queue {
  def apply[T](xs: T*) = new Queue[T](xs.toList, Nil)
}

class Queue[+T] protected (
	protected val leading: List[T],
	protected val trailing: List[T]
  ) {
  private def mirror =
    if (leading.isEmpty)
      new Queue(trailing.reverse, Nil)
    else
      this
      
  def head = mirror.leading.head
  
  def tail = {
    val q = mirror
    new Queue(q.leading.tail, q.trailing)
  }
  
  def enqueue[U >: T](x: U) =
    new Queue[U](leading, x :: trailing)
}
