package rtj.scala.advanced.lectures.section03

object PartialFunctionsLecture extends App {

  // Function[Int, Int]
  val aFunc = (x: Int) => x + 1

  // PartialFunction[Int, Int]
  val aFussyFunc: PartialFunction[Int, Int] = {
    case 1 => 43
    case 2 => 23
    case 3 => 2222
  }

  println(aFussyFunc(2))

  // this will crash with MatchError
  //println(aFussyFunc(20))

  // Int => Option[Int]
  println(aFussyFunc.lift.apply(20))

  println(aFussyFunc.orElse[Int, Int] {
    case 17 => 999
  }.apply(17))
}
