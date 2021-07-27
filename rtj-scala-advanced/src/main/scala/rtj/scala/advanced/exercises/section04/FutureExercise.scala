package rtj.scala.advanced.exercises.section04

import scala.concurrent.{Future, Promise}

object FutureExercise extends App {
  import scala.concurrent.ExecutionContext.Implicits.global

  def firstResult[A](fa: Future[A], fb: Future[A]):Future[A] = {
    val promise = Promise[A]

    fa.onComplete(promise.tryComplete)
    fb.onComplete(promise.tryComplete)

    promise.future
  }

  def retryUntil[A](action: () => Future[A], condition: A => Boolean): Future[A] = {
    action().filter(condition).recoverWith {
      case _: NoSuchElementException => retryUntil(action, condition)
    }
  }
}
