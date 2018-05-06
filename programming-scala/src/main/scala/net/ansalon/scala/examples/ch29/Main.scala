package net.ansalon.scala.examples.ch29

object Main {
  def main(args: Array[String]): Unit = {  
    val apple = persistence.PersistenceUnit.store.foodNamed("Apple").get
    println(apple)
    println(persistence.PersistenceUnit.browser.recipesUsing(apple))
  }
}