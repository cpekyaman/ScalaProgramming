package net.ansalon.scala.examples.ch26

object Main {
  def main(args: Array[String]): Unit = {
    printEmail("cenkpekyaman@yahoo.com")
    printEmail("cenk")

    printUpper("CENK")
    printUpper("cenk")

    printTwice("ceycey")
    printTwice("aliveli")

    val Decimal = """()?(\d+)(\.\d*)?""".r
    val input = "for 1.0 to 99 by 3"
    for (s <- Decimal findAllIn input)
      println(s)

    val Decimal(sign, integerpart, decimalpart) = "1.23"
    println("sign: " + sign + "; int: " + integerpart + "; dec: " + decimalpart)
  }
  
  private def printEmail(str: String) {
    str match {
      case Email(user, domain) => println("User " + user + " AT " + domain)
      case _ => println("NOT VALID EMAIL")
    }
  }
  
  private def printUpper(str: String) {
    str match {
      case UpperCase() => println("UPPER")
      case _ => println("NOT UPPER")
    }
  }
  
  private def printTwice(str: String) {
    str match {
      case Twice(str) =>	println("TWICE")
      case _	=> println("NOT TWICE")
    }
  }
}