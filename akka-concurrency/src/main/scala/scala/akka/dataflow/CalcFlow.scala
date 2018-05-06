package scala.akka.dataflow

import scala.async.Async._
import scala.concurrent.duration.Duration
import scala.concurrent.{Future,Await}
import scala.math.BigDecimal

object CalcFlow {
    import scala.concurrent.ExecutionContext.Implicits.global
    
    def calcPiTo(places: Int): Future[BigDecimal] = 
        Future(BigDecimal(1))
    
    def fibonacci(n: Int): Future[Seq[BigDecimal]] =
        Future(Seq(BigDecimal(0), BigDecimal(1), BigDecimal(1), BigDecimal(2)))
    
    def main(args: Array[String]) {
        val perfect = async {
            // 'pie' is now a Dataflow value, which is pi to 3,000,000 decimal places returned in a Future
            val pie = calcPiTo(3000000)
            
            // So is 'fibs', which is the first 31,402nd Fibonacci numbers returned in a Future
            val fibs = fibonacci(31402)
            
            // The 'perfect' area
            val lastFibs = await(fibs).last
            
            await(pie) * lastFibs * lastFibs
        }
        
        println(Await.result(perfect, Duration.Inf))
    }
}


