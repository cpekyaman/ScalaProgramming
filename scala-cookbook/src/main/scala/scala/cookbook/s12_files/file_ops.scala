import scala.io.Source
import scala.language.reflectiveCalls

object FileOps extends App {
    val fileName = "fileopen.in"

    type Closable = { def close(): Unit }
    
    // this line keeps the line open
    //Source.fromFile(fileName).getLines.foreach(println)

    def using[A <: Closable, B](resource: A)(func: A => B):B = {
        try{
            func(resource)
        } finally {
            resource.close()
        }
    } 

    using(Source.fromFile(fileName)) { src => 
        for(line <- src.getLines) {
            println(line)
        }
    }

    def getFilesWithExtensions(dir: String, extensions: Set[String]): List[java.io.File] = {
        new java.io.File(dir).listFiles.filter(_.isFile).toList.filter { file => 
            extensions.exists(file.getName.endsWith(_))
        }
    }

    val okFileExtensions = Set("wav", "mp3")
    val files = getFilesWithExtensions("/tmp", okFileExtensions)
    println(files)
}

