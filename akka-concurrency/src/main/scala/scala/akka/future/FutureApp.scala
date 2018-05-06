package scala.akka.future

import scala.concurrent.duration._
import scala.concurrent.{Future,Await}
import scala.util.{Try, Success, Failure}

object FutureApp {
	def main(args : Array[String]) {
	    import scala.concurrent.ExecutionContext.Implicits.global
	    
	    // traverse futures
	    val futures = (1 to 10) map { i => Future(i)}
	    val seqFuture = Future.traverse(futures) { fn =>
	      fn map { n => n * n }  
	    }
	    val seqResult = Await.result(seqFuture, 1.seconds)
	    println(s"traverse result is $seqResult")
	    
	    // recover example
	    val f = Future { 5 } filter { _ % 2 == 0 } recover {
	        case e: NoSuchElementException => 9
	    }
	    val result = Await.result(f, 1.seconds)
	    println(s"the result from recover is $result")
	    
	    // falback and onsuccess example
	    Future { 13 } filter { 
	        _ % 2 == 0
	    } fallbackTo Future {
	        "That didn't work"
	    } onSuccess {
	        case i: Int => println("Disco")
	        case m => println(s"Boogers $m")
	    }	
	    
	    Future{ scala.util.Random.nextInt(100) } filter { _ % 2 == 0 } onComplete {
	        case Success(num) => println(s"Success with $num")
	        case Failure(e) => println(s"system has failed with $e")
	    }
	    
	    // andThen
	    val fAndThen = Future { throw new Exception("Damn !!!") } andThen {
	        case Success(v) => println("Success in first andThen")
	        case Failure(t) => println(s"First failure: $t")
	    } andThen {
	        case Success(v) => println("Success in second andThen")
            case Failure(t) => println(s"Second failure: $t")
	    } andThen {
	        case Success(v) => println("Success in third andThen")
            case Failure(t) => println(s"Third failure: $t")
	    }
	    Await.result(fAndThen, 1.seconds)
	}
}
