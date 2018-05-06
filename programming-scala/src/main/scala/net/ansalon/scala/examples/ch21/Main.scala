package net.ansalon.scala.examples.ch21

class PreferredPrompt(val preference: String)
class PreferredDrink(val preference: String)

object Greeter {
	def greet(name: String)
			 (implicit prompt: PreferredPrompt, drink: PreferredDrink) 
	{
		println("Welcome, "+ name +". The system is ready.")
		print("But while you work, ")
		println("why not enjoy a cup of "+ drink.preference +"?")
		println(prompt.preference)
	}
}

object UserPrefs {
	implicit val prompt = new PreferredPrompt("Yes, master> ")
	implicit val drink = new PreferredDrink("tea")
}

object ListUtil {
	def max[T]
	    (elements: List[T])
		(implicit orderer: T => Ordered[T]): T =
		elements match {
			case List() =>
				throw new IllegalArgumentException("empty list!")
			case List(x) => x
			case x :: rest =>
				val maxRest = max(rest)
				if (x > maxRest) x
				else maxRest
		}
}

import UserPrefs._

object Main {
	def main(args: Array[String]): Unit = {
	  Greeter.greet("Joe")
	  
	  val maxInt = ListUtil.max(List(1,5,10,3))
	  println("maxInt= " + maxInt)
	  val maxStr = ListUtil.max(List("one", "two", "three"))
	  println("maxStr= " + maxStr)
	}
}