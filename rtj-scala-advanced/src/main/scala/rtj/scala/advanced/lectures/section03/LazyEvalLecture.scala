package rtj.scala.advanced.lectures.section03

object LazyEvalLecture extends App {

  // this does not fail until we use x
  lazy val x:Int = throw new RuntimeException

  def lt30(n: Int):Boolean = {
    println(s"is $n less than 30 ?")
    n < 30
  }

  def gt20(n: Int):Boolean = {
    println(s"is $n greater than 20 ?")
    n > 20
  }

  val numbers = List(1, 25, 40, 5, 23)
  val lt30Vals = numbers.filter(lt30)
  val gt20Vals = lt30Vals.filter(gt20)
  println(gt20Vals)

  println
  val lt30Lazy = numbers.withFilter(lt30)
  val gt20Lazy = lt30Lazy.withFilter(gt20)
  gt20Lazy.foreach(println)
}
