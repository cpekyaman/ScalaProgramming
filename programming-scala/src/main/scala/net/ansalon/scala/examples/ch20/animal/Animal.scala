package net.ansalon.scala.examples.ch20.animal

class Food

abstract class Animal {
	type FoodType <: Food
	def eat(f: FoodType): Unit
}