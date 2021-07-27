package rtj.scala.advanced.exercises.section02

object PatternMatchingExercise extends App {
  sealed trait DigitType
  case object SingleDigit extends DigitType
  case object DoubleDigit extends DigitType
  case object MultipleDigits extends DigitType

  sealed trait DivisibilityType
  case object Even extends DivisibilityType
  case object Odd extends DivisibilityType

  object Num {
    def unapply(number: Int): Option[(DigitType, DivisibilityType)] =
      Some(if(number < 10) SingleDigit else if(number < 100) DoubleDigit else MultipleDigits, if(number%2 == 0) Even else Odd)
  }

  def showNumType(num: Int): Unit = {
    num match {
      case Num(dgt, div) => println(s"$num is a $dgt and $div number")
      case _ => println("don't know what this is")
    }
  }

  showNumType(8)
  showNumType(45)
  showNumType(104)
}
