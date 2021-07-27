//package scala.cookbook.s12_files

import scala.language.reflectiveCalls
import scala.collection.mutable.ArrayBuffer

object CSVReader extends App {

    type Closable = { def close():Unit }
    
    def using[A <: Closable, B](res: A)(func: A => B):B = {
        try {
            func(res)
        } finally {
            res.close()
        }
    }

    val rows = ArrayBuffer[Array[String]]()

    using(scala.io.Source.fromFile("csv_file")) { src => 
        src.getLines.foreach { line =>
            rows += line.split(",").map(_.trim)
        }
    }

    rows.foreach { row => 
        println(s"${row(0)}|${row(1)}|${row(2)}|${row(3)}")
    }
}