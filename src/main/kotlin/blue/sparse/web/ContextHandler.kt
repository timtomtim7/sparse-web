package blue.sparse.web

interface ContextHandler {

	fun handle(result: MatchResult): String

	companion object {
		inline fun of(crossinline body: (MatchResult) -> String): ContextHandler {
			return object : ContextHandler {
				override fun handle(result: MatchResult) = body(result)
			}
		}
	}

}