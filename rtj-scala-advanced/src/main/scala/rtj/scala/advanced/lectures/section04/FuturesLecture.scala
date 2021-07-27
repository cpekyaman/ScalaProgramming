package rtj.scala.advanced.lectures.section04

import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}
import scala.concurrent.duration._

object FuturesLecture extends App {
  import scala.concurrent.ExecutionContext.Implicits.global

  def calculate: Int = {
    Thread.sleep(1000)
    42
  }

  val future = Future {
    calculate
  }

  future.onComplete {
    case Success(value) => println(s"got value $value")
    case Failure(exception) => println(s"got exception $exception")
  }

  case class Profile(id: Int, name:String) {
    def poke(friend: Profile): Unit = {
      println(s"$this poking $friend")
    }
  }

  object SocialNetwork {
    def fetchProfile(id: Int): Future[Profile] = Future {
      Thread.sleep(200)
      if(id == 0) {
        throw new NoSuchElementException
      }
      Profile(id, s"Person $id")
    }

    def findBestFriend(profile: Profile): Future[Profile] = Future {
      Thread.sleep(75)
      val friendId = profile.id * 100
      Profile(friendId, s"Person $friendId")
    }
  }

  for {
    me <- SocialNetwork.fetchProfile(1)
    you <- SocialNetwork.findBestFriend(me)
  } me.poke(you)

  val recoveringFuture = SocialNetwork.fetchProfile(0).recoverWith {
    case _: Throwable => SocialNetwork.fetchProfile(10)
  }
  println(Await.result(recoveringFuture, 1.seconds))

  val profileNameFuture = SocialNetwork.fetchProfile(1).map(_.name)
  println(Await.result(profileNameFuture, 1.seconds))
}
