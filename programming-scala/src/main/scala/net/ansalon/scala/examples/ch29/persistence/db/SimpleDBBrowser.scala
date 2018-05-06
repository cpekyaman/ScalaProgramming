package net.ansalon.scala.examples.ch29.persistence.db

import net.ansalon.scala.examples.ch29.persistence._
import net.ansalon.scala.examples.ch29.model._

object SimpleDBBrowser extends DataBrowser {
	val store = InMemoryDBStore
}