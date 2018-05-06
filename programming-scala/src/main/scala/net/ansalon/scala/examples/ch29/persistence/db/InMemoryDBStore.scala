package net.ansalon.scala.examples.ch29.persistence.db

import net.ansalon.scala.examples.ch29.persistence.PersistenceStore
import net.ansalon.scala.examples.ch29.model._

object InMemoryDBStore extends PersistenceStore {
	def allFoods: List[Food] = List(Apple, Orange, Cream, Sugar)
	def allRecipes: List[Recipe] = List(FruitSalad)
	
	override def foodNamed(name: String): Option[Food] =
		allFoods.find(_.name == name)	
}