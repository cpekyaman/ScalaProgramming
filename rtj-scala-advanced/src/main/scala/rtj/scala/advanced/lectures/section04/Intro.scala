package rtj.scala.advanced.lectures.section04

object Intro extends App {

  val thread = new Thread(() => println("I am running"))
  thread.start()
  thread.join()
}
