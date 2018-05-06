package net.ansalon.scala.examples.ch20.animal

class Fish extends Food
class Cat extends Animal {
	type FoodType = Fish
	override def eat(f: Fish): Unit = {println("Cats eat fish")}
}