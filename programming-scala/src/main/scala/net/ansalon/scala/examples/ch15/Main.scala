package net.ansalon.scala.examples.ch15

import exp._

object Main {
  def main(args: Array[String]): Unit = {
    val v = Var("x")
    val op = Binary("+", Number(1), v)

    println(v)
    println(op)
    
    println(desc(true))
    println(desc(Nil))
    println(desc("Cenk"))
    
    val capitals = Map("France" -> "Paris", "Japan" -> "Tokyo")
    println((capitals get "France"))
    println(show(capitals get "France"))
    println(capitals("France"))
    
  }

  def simplify(expr: Expr): Expr = expr match {
    case Unary("-", Unary("-", e)) => e // Double negation
    case Binary("+", e, Number(0)) => e // Adding zero
    case Binary("*", e, Number(1)) => e // Multiplying by one
    case _                         => expr
  }

  def desc(x: Any) = x match {
    case 5       => "five"
    case true    => "truth"
    case "hello" => "hi!"
    case Nil     => "the empty list"
    case _       => "something else"
  }

  def simplifyAll(expr: Expr): Expr = expr match {
    case Unary("-", Unary("-", e)) => simplifyAll(e) // ‘’ is its own inverse
    case Binary("+", e, Number(0)) => simplifyAll(e) // ‘0’ is a neutral element for ‘+’
    case Binary("*", e, Number(1)) => simplifyAll(e) // ‘1’ is a neutral element for ‘*’
    case Unary(op, e)              => Unary(op, simplifyAll(e))
    case Binary(op, l, r)          => Binary(op, simplifyAll(l), simplifyAll(r))
    case _                         => expr
  }
  
  def show(x: Option[String]) = x match {
	case Some(s) => s
	case None => "?"
  }
}