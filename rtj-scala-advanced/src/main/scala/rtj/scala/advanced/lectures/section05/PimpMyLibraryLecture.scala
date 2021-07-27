package rtj.scala.advanced.lectures.section05

import rtj.scala.advanced.lectures.section05.TypeClassesLecture.{HtmlSerializer, User}

object PimpMyLibraryLecture extends App {
  // used by HtmlEnrichment
  import TypeClassesLecture.userSerializer

  // type enrichment
  implicit class MyRichInt(val value: Int) extends AnyVal {
    def isEven: Boolean = value % 2 == 0

    def sqrt: Double = Math.sqrt(value)
  }

  // 42 is wrapper as a MyRichInt implicitly
  println(42.isEven)

  // powerful but not preferred
  implicit def strToInt(value: String): Int = Integer.valueOf(value)
  println("24" / 3)

  // enrichment type
  implicit class HtmlEnrichment[T](value: T) {
    def toHtml(implicit serializer: HtmlSerializer[T]): String = serializer.serialize(value)
  }
  // User is wrapped as HtmlEnrichment and toHtml received the implicit userSerializer as parameter
  println(User("John", 43).toHtml)
}
