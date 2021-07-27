package rtj.scala.advanced.lectures.section02

object PatternMatchingLecture extends App {

  abstract class MyList[+A] {
    def head:A = ???
    def tail:MyList[A] = ???
  }
  case object Empty extends MyList[Nothing]
  case class Cons[+A](override val head: A, override val tail: MyList[A]) extends MyList[A]

  object MyList {
    def unapplySeq[A](list: MyList[A]): Option[Seq[A]] =
      if(list == Empty) Some(Seq.empty) else unapplySeq(list.tail).map(list.head +: _)
  }

  def myList = Cons(1, Cons(2, Cons(3, Empty)))
  myList match {
    case MyList(1, 2, _*) => println("that's what we wanted")
    case _ => println("nope")
  }
}
