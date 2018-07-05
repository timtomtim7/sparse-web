package blue.sparse.web

import kotlinx.html.*
import kotlinx.html.stream.appendHTML
import org.eclipse.jetty.http.HttpStatus
import org.eclipse.jetty.server.*
import org.eclipse.jetty.server.handler.AbstractHandler
import java.io.File
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class WebServer(
		val contentRoot: File = File("run/src"),
		val port: Int = 80
) {
	val contexts: MutableMap<Regex, ContextHandler> = HashMap()
	val server = Server()

	init {
		server.handler = Handler()

		val config = HttpConfiguration()
		config.sendServerVersion = false

		val connector = ServerConnector(server, HttpConnectionFactory(config))
		connector.port = port

		server.connectors = arrayOf(connector)
	}

	fun start() {
		server.start()
		server.join()
	}

	private inner class Handler : AbstractHandler() {

		fun getContext(target: String): Pair<MatchResult, String> {
			var result: Pair<MatchResult, ContextHandler>? = null

			for((regex, handler) in contexts) {
				val match = regex.matchEntire(target)
				if(match != null) {
					result = match to handler
					break
				}
			}

			if(result == null)
				return matchAny.matchEntire(target)!! to target

			return result.first to result.second.handle(result.first)
		}

		override fun handle(
				target: String,
				baseRequest: Request,
				request: HttpServletRequest,
				response: HttpServletResponse
		) {
			val trimmedTarget = target.trim('/')
			val method = request.method.toUpperCase()
			println("[$method] $trimmedTarget")

			SparseWeb.updateContexts()
			val context = getContext(trimmedTarget)

			val file = File(contentRoot, context.second)

			response.contentType = "text/plain"
			if (file.extension == "kt")
				return sendError(response, baseRequest, 403)

			if (file.extension == "kts") {
				baseRequest.isHandled = true
				try {
					SparseWeb.scriptRequest(
							file,
							request,
							response,
							context.first
					)
				} catch (t: Throwable) {
					t.printStackTrace()
					return sendError(response, baseRequest, 500)
				}
				return
			}

			when {
				file.isDirectory -> file.listFiles().forEach {
					response.outputStream.println(it.name)
				}
				file.exists() -> response.outputStream.println(file.readText())
				else -> return sendError(response, baseRequest, 404)
			}

			baseRequest.isHandled = true
		}

	}

	private fun sendError(response: HttpServletResponse, baseRequest: Request, error: Int) {
		sendError(response, baseRequest, HttpStatus.getCode(error))
	}

	private fun sendError(response: HttpServletResponse, baseRequest: Request, error: HttpStatus.Code) {
		response.status = error.code
		baseRequest.isHandled = true
		response.contentType = "text/html"

		response.writer.appendHTML().html {
			body {
				h1 { +"Error ${error.code}: ${error.message}" }
				p { +"Hmm..." }
			}
		}
	}

	companion object {
		private val matchAny = Regex(".*", RegexOption.DOT_MATCHES_ALL)
	}
}