package rtj.scala.advanced.exercises.section03

object CurryingExercise extends App {
  def formatter(fmt: String)(x: Double): String = fmt.format(x)

  val simpleFormat = formatter("%4.2f") _
  val longFormat = formatter("%8.6f") _
  val longestFormat = formatter("%16.12f") _

  val numbers = List(3435.45454, 6757675.43434, 8098.3434)

  numbers.map(simpleFormat).foreach(println)
  println
  numbers.map(longFormat).foreach(println)
  println
  numbers.map(longestFormat).foreach(println)
}
