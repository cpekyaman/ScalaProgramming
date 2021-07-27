package rtj.scala.advanced.lectures.section06

// structural types use reflection to resolve calls
object StructuralTypesLecture extends App {

  type JClosable = java.io.Closeable

  class MyClosable {
    def close(): Unit = println("MyClosable closed")
  }

  // anything that has a close method is a closable
  type GenericClosable = { def close():Unit }

  def closeQuitely(closable: GenericClosable): Unit = closable.close()

  closeQuitely(new JClosable {
    override def close(): Unit = println("java io closable")
  })
  closeQuitely(new MyClosable)
}
