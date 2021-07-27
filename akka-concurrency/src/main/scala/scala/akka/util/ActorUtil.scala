package scala.akka.util

import akka.actor.{ActorContext,ActorRef}
import scala.concurrent.{Await,Future}
import akka.util.Timeout

object ActorUtil {

    import scala.concurrent.duration._
    implicit val timeout = Timeout(1.second)

    def actorFor(context:ActorContext, path: String): ActorRef = Await.result(context.actorSelection(path).resolveOne(), 1.seconds)
}