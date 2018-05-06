package net.ansalon.scala.examples.ch29.persistence

import net.ansalon.scala.examples.ch29.persistence.db._

object PersistenceUnit {
	def store: PersistenceStore = InMemoryDBStore 
	def browser: DataBrowser = SimpleDBBrowser
}