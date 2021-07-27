package rtj.scala.advanced.lectures.section04

object ThreadCommunicationLecture extends App {

  class ValueContainer[T] {
    private var value: Option[T] = None

    def isEmpty:Boolean = value.isEmpty
    def set(newVal: T):Unit = this.value = Some(newVal)
    def get():T = this.value.get
  }

  val container = new ValueContainer[String]

  def busyWait(): Unit = {
    val consumer = new Thread(() => {
      println("consumer running")
      while(container.isEmpty) {
        println("no value in consumer")
      }
    })

    val producer = new Thread(() => {
      println("producer running")
      Thread.sleep(500)
      container.set("hello")
      println("producer set value")
    })

    consumer.start()
    producer.start()
  }
}
