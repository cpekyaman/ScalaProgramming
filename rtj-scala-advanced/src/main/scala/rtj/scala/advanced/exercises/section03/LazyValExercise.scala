package rtj.scala.advanced.exercises.section03

import scala.annotation.tailrec

abstract class MyStream[+A] {
  def isEmpty:Boolean
  def head: A
  def tail:MyStream[A]

  // prepend
  def #::[B >: A](e: B):MyStream[B]
  // concat
  // call by name ıs mandatory to enable call when needed semantics
  // otherwise ++ can break lazy evaluation of stream
  def ++[B >: A](s: => MyStream[B]): MyStream[B]

  def take(n: Int): MyStream[A]

  def map[B](f: A => B): MyStream[B]
  def flatMap[B](f: A => MyStream[B]):MyStream[B]
  def filter(p: A => Boolean): MyStream[A]
  def foreach(f: A => Unit): Unit

  @tailrec
  final def toList[B >: A](acc: List[B] = Nil): List[B] = {
    if(isEmpty) acc.reverse else tail.toList(head :: acc)
  }
}

object EmptyStream extends MyStream[Nothing] {
  override def isEmpty: Boolean = true

  override def head: Nothing = throw new NoSuchElementException
  override def tail: MyStream[Nothing] = throw new NoSuchElementException

  override def #::[B](e: B): MyStream[B] = new ConcreteStream[B](e, this)
  override def ++[B](s: => MyStream[B]): MyStream[B] = s

  override def take(n: Int): MyStream[Nothing] = this

  override def map[B](f: Nothing => B): MyStream[B] = this
  override def flatMap[B](f: Nothing => MyStream[B]): MyStream[B] = this
  override def filter(p: Nothing => Boolean): MyStream[Nothing] = this
  override def foreach(f: Nothing => Unit): Unit = ()
}

class ConcreteStream[+A](hd: A, tl: => MyStream[A]) extends MyStream[A] {
  override def isEmpty: Boolean = false

  override val head: A = hd
  override lazy val tail: MyStream[A] = tl

  override def #::[B >: A](e: B): MyStream[B] = new ConcreteStream[B](e, this)
  override def ++[B >: A](s: => MyStream[B]): MyStream[B] = new ConcreteStream[B](head, tail ++ s)

  override def take(n: Int): MyStream[A] =
    if(n == 1) new ConcreteStream[A](head, EmptyStream) else new ConcreteStream[A](head, tail.take(n - 1))

  override def map[B](f: A => B): MyStream[B] = new ConcreteStream[B](f(head), tail.map(f))
  override def flatMap[B](f: A => MyStream[B]): MyStream[B] = f(head) ++ tail.flatMap(f)
  override def filter(p: A => Boolean): MyStream[A] =
    if(p(head)) new ConcreteStream[A](head, tail.filter(p)) else tail.filter(p)
  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail.foreach(f)
  }
}

object MyStream {
  def from[A](start: A)(generator: A => A): MyStream[A] =
    new ConcreteStream[A](start, MyStream.from(generator(start))(generator))
}

object LazyValExercise extends App {
  val naturals = MyStream.from(0)(_ + 1)
  naturals.take(10000).foreach(println)

  def fibonacci(first: Int, second: Int): MyStream[Int] =
    // tail is lazily evaluated, otherwise it would blow up
    new ConcreteStream[Int](first, fibonacci(second, first + second))
}
