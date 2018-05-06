package net.ansalon.scala.examples.ch26

object Email {
	def apply(user: String, domain: String) = user + "@" + domain
	
	def unapply(email: String): Option[(String, String)] = {
		val parts = email split "@"
		if(parts.length == 2) Some(parts(0), parts(1)) else None
	}	  
}

object Twice {
	def apply(s: String): String = s + s
	
	def unapply(s: String): Option[String] = {
		val length = s.length / 2
		val half = s.substring(0, length)
		if (half == s.substring(length)) Some(half) else None
	}
}

object UpperCase {
  //def apply(s: String): String = s
  def unapply(s: String): Boolean = s.toUpperCase == s
}