package net.ansalon.scala.examples.ch29.persistence

import net.ansalon.scala.examples.ch29.model._

trait PersistenceStore {
  def allFoods: List[Food]
  def allRecipes: List[Recipe]
  
  def foodNamed(name: String): Option[Food] =
	  allFoods.find(f => f.name == name)
}