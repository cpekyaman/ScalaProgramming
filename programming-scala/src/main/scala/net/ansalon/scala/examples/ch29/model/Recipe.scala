package net.ansalon.scala.examples.ch29.model

class Recipe(
	val name: String,
	val ingredients: List[Food],
	val instructions: String
) {
	override def toString = name
}

object FruitSalad extends Recipe(
	"fruit salad",
	List(Apple, Orange, Cream, Sugar),
	"Stir it all together."
)