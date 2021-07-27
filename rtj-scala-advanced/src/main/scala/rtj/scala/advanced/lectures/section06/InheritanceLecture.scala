package rtj.scala.advanced.lectures.section06

object InheritanceLecture extends App {

  trait Writable[T] {
    def write(): Unit
  }
  trait Closable {
    def close(status: Int)
  }
  trait GenericStream[T] {
    def foreach(f: T => Unit): Unit
  }

  def processStream[T <: Writable[T]](stream: GenericStream[T] with Closable): Unit = {
    stream.foreach(_.write())
    stream.close(1)
  }
}
