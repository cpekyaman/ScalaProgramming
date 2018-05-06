package net.ansalon.scala.examples.ch28

abstract class User {
	val username: String
	val firstName: String
	val lastName: String
	val email: String
	val birthYear: Int
	
	def toXML = {
	  <user>
		<username>{username}</username>
		<firstName>{firstName}</firstName>
		<lastName>{lastName}</lastName>
		<email>{email}</email>
		<birthYear>{birthYear}</birthYear>
	  </user>
	}
	
	override def toString = username	
}

object User {
	def fromXML(root: xml.Node): User = {
	  new User {
	    val username 	= (root \ "username").text
	    val firstName 	= (root \ "firstName").text
	    val lastName 	= (root \ "lastName").text
	    val email 		= (root \ "email").text
	    val birthYear 	= (root \ "birthYear").text.toInt
	  }
	}
}