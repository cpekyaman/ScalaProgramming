package net.ansalon.scala.examples.ch28

object Main {
	def main(args: Array[String]): Unit = {
	  val cenk = new User {
	    val username 	= "cpekyaman"
	    val firstName	= "cenk"
	    val lastName	= "pekyaman"
	    val email		= "cenkpekyaman@yahoo.com"
	    val birthYear	= 1980
	  }
	  
	  val cnkXML = cenk.toXML
	  xml.XML.save("users.xml", cnkXML)
	  
	  val loadnode = xml.XML.loadFile("users.xml")
	  val cenk2 = User.fromXML(loadnode)
	  
	  println(cenk2)
	}
}