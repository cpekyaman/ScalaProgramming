package rtj.scala.advanced.exercises.section03

trait FunctionalSet[A] extends (A => Boolean) {
  override def apply(e: A): Boolean = contains(e)
  def unary_! : FunctionalSet[A]

  def contains(e: A): Boolean

  def +(e: A): FunctionalSet[A]
  def ++(fs: FunctionalSet[A]):FunctionalSet[A]

  def map[B](f: A => B): FunctionalSet[B]
  def flatMap[B](f: A => FunctionalSet[B]): FunctionalSet[B]
  def filter(p: A => Boolean):FunctionalSet[A]
  def foreach(f: A => Unit):Unit

  def -(e: A):FunctionalSet[A]
  def &(set: FunctionalSet[A]):FunctionalSet[A]
  def --(set: FunctionalSet[A]):FunctionalSet[A]
}

class EmptyFunctionalSet[A] extends FunctionalSet[A] {
  override def contains(e: A): Boolean = false
  override def unary_! : FunctionalSet[A] = new PropertyBasedSet[A](_ => true)

  override def +(e: A): FunctionalSet[A] = new ConcreteFunctionalSet(e, this)
  override def ++(fs: FunctionalSet[A]): FunctionalSet[A] = fs

  override def map[B](f: A => B): FunctionalSet[B] = new EmptyFunctionalSet[B]
  override def flatMap[B](f: A => FunctionalSet[B]): FunctionalSet[B] = new EmptyFunctionalSet[B]
  override def filter(p: A => Boolean): FunctionalSet[A] = this
  override def foreach(f: A => Unit): Unit = ()

  override def -(e: A): FunctionalSet[A] = this
  override def &(set: FunctionalSet[A]): FunctionalSet[A] = this
  override def --(set: FunctionalSet[A]): FunctionalSet[A] = this
}

class ConcreteFunctionalSet[A](head: A, tail: FunctionalSet[A]) extends FunctionalSet[A] {
  override def contains(e: A): Boolean = e == head || tail.contains(e)
  override def unary_! : FunctionalSet[A] = new PropertyBasedSet[A](!this.contains(_))

  override def +(e: A): FunctionalSet[A] =
    if(this contains e) this else new ConcreteFunctionalSet[A](e, this)
  override def ++(fs: FunctionalSet[A]): FunctionalSet[A] = tail ++ fs + head

  override def map[B](f: A => B): FunctionalSet[B] = (tail map f) + f(head)
  override def flatMap[B](f: A => FunctionalSet[B]): FunctionalSet[B] = (tail flatMap f) ++ f(head)
  override def filter(p: A => Boolean): FunctionalSet[A] = if(p(head)) (tail filter p) + head else tail filter p
  override def foreach(f: A => Unit): Unit = {
    f(head)
    tail foreach f
  }

  override def -(e: A): FunctionalSet[A] = if(e == head) tail else tail - e + head
  override def &(set: FunctionalSet[A]): FunctionalSet[A] = this.filter(set)
  override def --(set: FunctionalSet[A]): FunctionalSet[A] = this.filter(e => ! set(e))
}

class PropertyBasedSet[A](property: A => Boolean) extends FunctionalSet[A] {
  override def apply(e: A): Boolean = property(e)
  def unary_! : FunctionalSet[A] = new PropertyBasedSet[A](x => ! property(x))

  def contains(e: A): Boolean = property(e)

  def +(e: A): FunctionalSet[A] = new PropertyBasedSet[A](x => property(x) || x == e)
  def ++(fs: FunctionalSet[A]):FunctionalSet[A] = new PropertyBasedSet[A](x => property(x) || fs(x))

  // we don't know how the result sets would be so we can't implement them for now
  def map[B](f: A => B): FunctionalSet[B] = ???
  def flatMap[B](f: A => FunctionalSet[B]): FunctionalSet[B] = ???
  // technically we don't know what elements we have
  def foreach(f: A => Unit):Unit = ???

  def filter(p: A => Boolean):FunctionalSet[A] = new PropertyBasedSet[A](x => property(x) && p(x))

  def -(e: A):FunctionalSet[A] = filter(x => x != e)
  def &(set: FunctionalSet[A]):FunctionalSet[A] = filter(set)
  def --(set: FunctionalSet[A]):FunctionalSet[A] = filter(!set)
}

class AllInclusiveFunctionalSet[A] extends FunctionalSet[A] {
  override def unary_! : FunctionalSet[A] = new EmptyFunctionalSet[A]
  override def contains(e: A): Boolean = true

  override def +(e: A): FunctionalSet[A] = this
  override def ++(fs: FunctionalSet[A]): FunctionalSet[A] = this

  override def map[B](f: A => B): FunctionalSet[B] = ???
  override def flatMap[B](f: A => FunctionalSet[B]): FunctionalSet[B] = ???
  override def filter(p: A => Boolean): FunctionalSet[A] = ???
  override def foreach(f: A => Unit): Unit = ()

  override def -(e: A): FunctionalSet[A] = ???
  override def &(set: FunctionalSet[A]): FunctionalSet[A] = filter(set)
  override def --(set: FunctionalSet[A]): FunctionalSet[A] = filter(!set)
}

object FunctionalSet {
  def apply[A](values: A*):FunctionalSet[A] = {
    @scala.annotation.tailrec
    def buildSet(vs: Seq[A], set: FunctionalSet[A]):FunctionalSet[A] = {
      if(vs.isEmpty) set else buildSet(vs.tail, set + vs.head)
    }
    buildSet(values.toSeq, new EmptyFunctionalSet[A])
  }
}

object FunctionalSetApp extends App {
  val set = FunctionalSet(1,2,3,4,5)
  set foreach println
  set.map(_ * 10).foreach(println)
}
