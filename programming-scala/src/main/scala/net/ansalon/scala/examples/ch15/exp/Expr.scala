package net.ansalon.scala.examples.ch15.exp

sealed abstract class Expr
case class Var(name: String) extends Expr
case class Number(num: Double) extends Expr
case class Unary(operator: String, arg: Expr) extends Expr
case class Binary(operator: String, left: Expr, right: Expr) extends Expr
