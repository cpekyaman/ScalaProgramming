package net.ansalon.scala.examples.ch20.animal

class Grass extends Food
class Cow extends Animal {
	type FoodType = Grass
	override def eat(f: Grass): Unit = {println("Cow eats Grass") }
}