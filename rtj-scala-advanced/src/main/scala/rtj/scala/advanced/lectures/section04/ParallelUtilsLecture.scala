package rtj.scala.advanced.lectures.section04

object ParallelUtilsLecture extends App {

  // list is processed ın parallel (probably not worth for this size)
  val parList = (1 to 10000).toList.par
  parList.map(_ * 2).foreach(println)
}
