package rtj.scala.advanced.exercises.section03

class LazyVal[+A](value: => A) {
  private lazy val _value = value
  def flatMap[B](f: (=> A) => LazyVal[B]): LazyVal[B] = f(_value)

  override def toString: String = _value.toString
}

object LazyVal {
  def apply[A](vf: => A): LazyVal[A] = new LazyVal(vf)
}

object MonadExercise extends App {
  val lv = LazyVal {
    println("I am lazy")
    54
  }
  println(lv)
  println(lv.flatMap(v => LazyVal(v * 2)))

  // map written with flatMap
  println(List(1,2,3).map(_ * 2))
  println(List(1,2,3).flatMap(x => List(x * 2)))
}
