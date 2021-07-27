package rtj.scala.advanced.exercises.section05

object ImplicitsExercise extends App {

  case class Person(name: String, age: Int)
  object Person {
    implicit val nameOrdering: Ordering[Person] = Ordering.fromLessThan(_.name < _.name)
  }

  // this takes precedence, implicit in companion ıs ignored
  implicit val ageOrdering: Ordering[Person] = Ordering.fromLessThan(_.age < _.age)
  val persons = List(Person("Billy", 40), Person("John", 28), Person("Alex", 35))
  println(persons.sorted)
}
