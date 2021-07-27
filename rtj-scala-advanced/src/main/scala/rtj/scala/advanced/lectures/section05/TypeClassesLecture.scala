package rtj.scala.advanced.lectures.section05

object TypeClassesLecture extends App {
  // type class with companion
  trait HtmlSerializer[T] {
    def serialize(value: T): String
  }
  object HtmlSerializer {
    def serialize[T](value: T)(implicit serializer: HtmlSerializer[T]): String = serializer.serialize(value)

    def apply[T](implicit serializer: HtmlSerializer[T]): HtmlSerializer[T] = serializer
  }

  case class User(name: String, age: Int)
  // type class instance
  object UserSerializer extends HtmlSerializer[User] {
    override def serialize(value: User): String = s"${value.name} ${value.age}"
  }

  implicit val userSerializer:HtmlSerializer[User] = UserSerializer
  val john = User("John", 32)
  // serializer picks up correct serializer vıa implicits
  println(HtmlSerializer.serialize(john))
}
