package scala.akka.agents

import scala.concurrent.duration.Duration

sealed abstract class Gender
case object Male extends Gender
case object Female extends Gender

case class GenderAndTime(gender: Gender, peakDuration: Duration, count: Int)

