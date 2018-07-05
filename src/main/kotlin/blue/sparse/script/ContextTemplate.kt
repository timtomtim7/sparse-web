package blue.sparse.script

import blue.sparse.web.ContextHandler
import blue.sparse.web.SparseWeb

abstract class ContextTemplate {

	init {
		SparseWeb.server.contexts.clear()
	}

	fun context(regex: String, result: String) {
		context(Regex(regex), result)
	}

	fun context(regex: String, result: (MatchResult) -> String) {
		context(Regex(regex), result)
	}

	fun context(regex: Regex, result: String) {
		context(regex) { result }
	}

	fun context(regex: Regex, result: (MatchResult) -> String) {
		SparseWeb.server.contexts[regex] = ContextHandler.of(result)
	}

}