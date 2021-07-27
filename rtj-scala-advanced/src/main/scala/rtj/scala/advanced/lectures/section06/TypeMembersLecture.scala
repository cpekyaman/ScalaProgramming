package rtj.scala.advanced.lectures.section06

object TypeMembersLecture extends App {

  class Animal
  class Dog extends Animal
  class Cat extends Animal

  class AnimalCollection {
    type AnimalType

    def add(animal: AnimalType): AnimalCollection = ???
  }

  class CatCollection extends AnimalCollection {
    override type AnimalType = Cat
  }

}
