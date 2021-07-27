package rtj.scala.advanced.lectures.section03

trait Attempt[+A] {
  def flatMap[B](f: A => Attempt[B]): Attempt[B]
}
case class Success[+A](value: A) extends Attempt[A] {
  override def flatMap[B](f: A => Attempt[B]): Attempt[B] =
    try {
      f(value)
    } catch {
      case e: Throwable => Fail(e)
    }
}
case class Fail(e: Throwable) extends Attempt[Nothing] {
  override def flatMap[B](f: Nothing => Attempt[B]): Attempt[B] = this
}

object Attempt {
  def apply[A](a: => A): Attempt[A] =
    try {
      Success(a)
    } catch {
      case e: Throwable => Fail(e)
    }
}

object MonadLecture extends App {
  // left identity
  println(Attempt(42).flatMap(v => Attempt(v.toString)))
  println(Attempt(42.toString))

  // right identity
  val atm = Attempt("34".toInt)
  println(atm)
  println(atm.flatMap(v => Attempt.apply(v)))
}
