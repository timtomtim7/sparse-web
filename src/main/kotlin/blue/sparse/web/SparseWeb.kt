package blue.sparse.web

import blue.sparse.script.DynamicClassManager
import blue.sparse.script.ScriptManager
import java.io.File
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

object SparseWeb {
	val dynamic = DynamicClassManager
	val scriptManager = ScriptManager()
	val server = WebServer()

	init {
		recompileNonScripts()
	}

	fun recompileNonScripts() {
		val compiled = dynamic.compileAll(File("run/src"))
		scriptManager.parentClassLoader = compiled
		scriptManager.addClassPathFile(compiled.jar)
	}

	fun scriptRequest(
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


//	for(i in 1..10) {
//		val ms = measureTimeMillis {
//			val dcl = DynamicClassManager.compileAll(File("run/src"))
//			val scriptManager = ScriptManager(dcl)
//			scriptManager.addClassPathFile(dcl.jar)
//			scriptManager.get()
//		}
//		println("Took ${ms}ms")
//	}

}