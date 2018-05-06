package net.ansalon.scala.examples.ch15

import exp._
import net.ansalon.scala.examples.ch10.Element
import net.ansalon.scala.examples.ch10.Element.elem

class ExprFormatter {
  private val opGroups = Array(
    Set("|", "||"),
    Set("&", "&&"),
    Set("^"),
    Set("==", "!="),
    Set("<", "<=", ">", ">="),
    Set("+", "-"),
    Set("*", "%")
  )

  private val opPrecedences = {
    val assocs = for {
      i <- 0 until opGroups.length
      op <- opGroups(i)
    } yield op -> i
    assocs.toMap
  }

  private val unaryPrecedence = opGroups.length
  private val divisionPrecedence = -1

  private def format(e: Expr, enclPrec: Int): Element = {
    e match {
      case Var(name) => elem(name)
      
      case Number(num) =>
        def strip(s: String): String =
          if (s endsWith ".0") s.substring(0, s.length - 2) else s
        elem(strip(num.toString))
        
      case Unary(op, arg) => elem(op) beside format(arg, unaryPrecedence)
      
      case Binary("/", left, right) =>
        val top = format(left, divisionPrecedence)
        val bot = format(right, divisionPrecedence)
        val line = elem('-', top.width max bot.width, 1)
        val frac = top above line above bot
        
        if (enclPrec != divisionPrecedence) frac
        else elem(" ") beside frac beside elem(" ")
        
      case Binary(op, left, right) =>
        val opPrec = opPrecedences(op)
        val l = format(left, opPrec)
        val r = format(right, opPrec + 1)
        val oper = l beside elem(" " + op + " ") beside r
        
        if (enclPrec <= opPrec) oper
        else elem("(") beside oper beside elem(")")
    }
  }
  
  def format(e: Expr): Element = format(e, 0)
}