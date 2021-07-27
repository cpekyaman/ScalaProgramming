package rtj.scala.advanced.lectures.section05

object ImplicitsLecture extends App {

  // implicit ->
  val intPair = 2 -> 5

  def multiply(num: Int)(implicit mul: Int): Int = num * mul
  implicit val defaultMul:Int = 4

  println(multiply(3))

  implicit val reverseOrder:Ordering[Int] = Ordering.fromLessThan[Int](_ > _)
  println(List(1,4,2,6,3,7).sorted)
}
