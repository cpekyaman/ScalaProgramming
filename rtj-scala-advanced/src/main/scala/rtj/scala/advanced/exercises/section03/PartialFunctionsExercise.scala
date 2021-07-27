package rtj.scala.advanced.exercises.section03

object PartialFunctionsExercise extends App {
  val inputHandler = new PartialFunction[String, String] {
    override def isDefinedAt(in: String): Boolean = in match {
      case "Hello" => true
      case "Goodbye" => true
      case "How are you ?" => true
      case _ => false
    }

    override def apply(in: String): String = in match {
      case "Hello" => "Hi there"
      case "Goodbye" => "Whatever"
      case "How are you ?" => "Fine"
    }
  }

  scala.io.Source.stdin.getLines().foreach(line => println(inputHandler.apply(line)))
}
