package blue.sparse.web

import blue.sparse.script.*
import java.io.File
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

object SparseWeb {
	val dynamic = DynamicClassManager
	val scriptManager = ScriptManager()
	val server = WebServer()

	private var lastContextUpdate = 0L

	init {
		File("run/temp").apply {
			deleteRecursively()
			mkdirs()
		}
		recompileNonScripts()
	}

	internal fun updateContexts() {
		val file = File("run/context.kts")
		if(!file.exists())
			return

		if(lastContextUpdate >= file.lastModified())
			return

		val script = scriptManager[file, ContextTemplate::class]
		val constructor = script.clazz.constructors.first()
		constructor.newInstance()
		lastContextUpdate = System.currentTimeMillis()
	}

	private fun recompileNonScripts() {
		val compiled = dynamic.compileAll(
				File("run/src")
		)
		scriptManager.clearClassPath()
		scriptManager.parentClassLoader = compiled
		if(compiled != null) {
			scriptManager.addClassPathFile(compiled.jar)
		}
	}

	internal fun scriptRequest(
			file: File,
			request: HttpServletRequest,
			response: HttpServletResponse,
			context: MatchResult
	) {
		if(dynamic.filesChanged)
			recompileNonScripts()

		val script = scriptManager[file]
		val constructor = script.clazz.constructors.first()
		constructor.newInstance(request, response, context)
	}

	fun start() {
		server.start()
	}
}

fun main(args: Array<String>) {
	SparseWeb.start()
}