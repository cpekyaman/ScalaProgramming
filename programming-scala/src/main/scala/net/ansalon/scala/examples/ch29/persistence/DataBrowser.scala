package net.ansalon.scala.examples.ch29.persistence

import net.ansalon.scala.examples.ch29.model._

trait DataBrowser {
	val store: PersistenceStore
	
	def recipesUsing(food: Food): List[Recipe] =
	  store.allRecipes.filter(recipe => recipe.ingredients.contains(food))
}