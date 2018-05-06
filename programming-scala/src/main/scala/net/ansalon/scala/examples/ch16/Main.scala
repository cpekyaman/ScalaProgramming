package net.ansalon.scala.examples.ch16

object Main {

  def main(args: Array[String]): Unit = {
	val list = List(4, 2, 5, 1, 3)
	val sorted = isort(list)
	
	println(sorted)
	
	val list2 = List.range(1,2700) map (i => List.range(1, i % 5))
	var start = System.currentTimeMillis
	var result = flattenLeft(list2)
	var end = System.currentTimeMillis
	
	val left = (end - start)
	println("flattenLeft: " + left)
	println("List Size: " + result.length)
	
	val list3 = List.range(1,2700) map (i => List.range(1, i % 5))
	start = System.currentTimeMillis
	result = flattenRight(list3)
	end = System.currentTimeMillis
	
	val right = (end - start)	
	println("flattenRight: " + right)
	println("List Size: " + result.length)
  }

  def isort(xs: List[Int]): List[Int] =
    if (xs.isEmpty) Nil
    else insert(xs.head, isort(xs.tail))
    
  def insert(x: Int, xs: List[Int]): List[Int] =
    if (xs.isEmpty || x <= xs.head) x :: xs
    else xs.head :: insert(x, xs.tail)
    
  def msort[T](less: (T,T) => Boolean)(xs: List[T]): List[T] = {
      def merge(xs: List[T], ys: List[T]): List[T] = {
        (xs, ys) match {
        	case (Nil, _) => ys
	        case (_, Nil) => xs
	        case (x :: xs1, y :: ys1) =>
	          if(less(x, y)) x :: merge(xs1, ys)
	          else y :: merge(xs, ys1)
        }
      }
      
      val splitPos = xs.length / 2
      if(splitPos == 0) xs
      else {
        val (ys, zs) = xs splitAt splitPos
        merge(msort(less)(ys), msort(less)(zs))
      }
  }
  
  def flattenLeft[T](xss: List[List[T]]) =
	(List[T]() /: xss) (_ ::: _)
  def flattenRight[T](xss: List[List[T]]) =
	(xss :\ List[T]()) (_ ::: _)
}