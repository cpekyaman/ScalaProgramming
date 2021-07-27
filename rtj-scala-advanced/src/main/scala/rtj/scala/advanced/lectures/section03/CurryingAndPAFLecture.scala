package rtj.scala.advanced.lectures.section03

object CurryingAndPAFLecture extends App {
  val adderFactory:Int => Int => Int = x => y => x + y
  val add3 = adderFactory(3)
  println(add3(4))

  def curriedAdder(x: Int) (y: Int): Int = x + y
  val add4:Int => Int = curriedAdder(4)
  println(add4(7))

  val add5 = curriedAdder(5) _
  println(add5(9))

  def adder(x: Int, y: Int):Int = x + y
  val add2 = (x:Int) => adder(2, x)
  val add2_2 = adder(2, _:Int)

  val adderF = (x:Int, y:Int) => x + y
  val add2_3 = adderF.curried(2)
}
