package net.ansalon.scala.examples.ch09

object FileMatcher {
	private def files = (new java.io.File(".")).listFiles
		
	def filesEnding(query: String) =
		filesMatching(_.endsWith(query)) // (fileName, query) => fileName.endsWith(query)
	def filesContaining(query: String) =
		filesMatching(_.contains(query)) // (fileName, query) => fileName.contains(query)
	def filesRegex(query: String) =
		filesMatching(_.matches(query))  // (fileName, query) => fileName.matches(query)

	// high level function
	// taking low level function as parameter
	def filesMatching(
			matcher: (String) => Boolean) = {
		for (file <- files if matcher(file.getName))
		yield file
	}
}