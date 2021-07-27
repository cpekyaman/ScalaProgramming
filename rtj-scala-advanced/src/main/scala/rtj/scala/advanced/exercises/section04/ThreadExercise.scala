package rtj.scala.advanced.exercises.section04

class InceptionThread(val index: Int, val max: Int) extends Thread {
  override def run(): Unit = {
    if(index < max) {
      val nested:InceptionThread = new InceptionThread(index + 1, max)
      nested.start()
      nested.join()
    }
    println(s"Hello from thread $index")
  }
}
object ThreadExercise extends App {
  val thread = new InceptionThread(1, 50)
  thread.start()
  thread.join()
}
