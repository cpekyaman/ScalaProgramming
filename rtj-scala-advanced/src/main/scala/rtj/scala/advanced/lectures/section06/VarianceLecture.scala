package rtj.scala.advanced.lectures.section06

// method args are in contravariant position
// method return types are in covariant position
object VarianceLecture extends App {

  trait Animal {
    def name: String
  }
  case class Dog(override val name: String) extends Animal
  case class Cat(override val name: String) extends Animal
  case class Crocodile(override val name: String) extends Animal

  // covariant type
  // ctor param can only be val, not var
  class CovCage[+T](val animal: T)
  val cage:CovCage[Animal] = new CovCage[Cat](Cat("hhh"))

  // invariant type
  class InvCage[T](var animal: T)
  // this won't compile, type is invariant
  // val icage: InvCage[Animal] = new InvCage[Dog]

  class ContCage[-T]//(val animal: T)
  val ccage: ContCage[Cat] = new ContCage[Animal]

  class PetShop[-T] {
    // this won't compile
    // def get(): T
    def get[S <: T](defaultVal: S): S = defaultVal
  }
}
