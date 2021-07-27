package rtj.scala.advanced.lectures.section05

object MagnetPatternLecture extends App {
  trait MessageMagnet[Result] {
    def apply(): Result
  }

  trait Actor {
    def receive[R](mm: MessageMagnet[R]): Unit
  }

  object PrintingActor extends Actor {
    override def receive[R](mm: MessageMagnet[R]): Unit = println(mm())
  }

  implicit class StringToMagnet(value: String) extends MessageMagnet[String] {
    override def apply(): String = {
      println("StringToMagnet")
      value
    }
  }

  implicit class IntToMagnet(value: Int) extends MessageMagnet[Int] {
    override def apply(): Int = {
      println("IntToMagnet")
      value
    }
  }

  PrintingActor.receive("cenk")
  PrintingActor.receive(2)
}
