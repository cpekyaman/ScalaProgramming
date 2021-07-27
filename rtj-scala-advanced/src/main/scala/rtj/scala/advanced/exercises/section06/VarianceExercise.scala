package rtj.scala.advanced.exercises.section06

// collection of things: covariant
// group of actions: contravariant
object VarianceExercise extends App {

  trait Vehicle
  class Bike extends Vehicle
  class Car extends Vehicle
  class Bus extends Vehicle

  class IParking[T](val vehicles: List[T]) {
    def park(v: T): IParking[T] = ???
    def checkout(cond: String):List[T] = ???

    def flatMap[S](f: T => IParking[S]): IParking[S] = ???
  }

  class CovParking[+T](val vehicles: List[T]) {
    def park[S >: T](v: S): CovParking[S] = ???
    def checkout(cond: String):List[T] = ???

    def flatMap[S](f: T => CovParking[S]): CovParking[S] = ???
  }

  class ContParking[-T] {
    def park(v: T): ContParking[T] = ???
    def checkout[S <: T](cond: String):List[S] = ???

    def flatMap[R <: T, S](f: R => ContParking[S]): ContParking[S] = ???
  }
}
