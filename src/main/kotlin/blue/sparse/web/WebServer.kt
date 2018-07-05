package blue.sparse.web

import org.eclipse.jetty.server.*
import org.eclipse.jetty.server.handler.AbstractHandler
import java.io.File
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class WebServer(
		val contentRoot: File = File("run/src"),
		val port: Int = 80
) {

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
		override fun handle(
				target: String,
				baseRequest: Request,
				request: HttpServletRequest,
				response: HttpServletResponse
		) {
			val trimmedTarget = target.trim('/')
			val method = request.method.toUpperCase()
			println("[$method] $trimmedTarget")

			//TODO: Context matching
			val file = File(contentRoot, trimmedTarget)

			response.contentType = "text/plain"
			if(file.extension == "kt") {
				response.status = 403
				baseRequest.isHandled = true
				return
			}

			if(file.extension == "kts") {
				baseRequest.isHandled = true
				SparseWeb.scriptRequest(
						file,
						request,
						response,
						matchAny.matchEntire(trimmedTarget)!!
				)
				return
			}

			when {
				file.isDirectory -> file.listFiles().forEach {
					response.outputStream.println(it.name)
				}
				file.exists() -> response.outputStream.println(file.readText())
				else -> response.status = 404
			}

			baseRequest.isHandled = true
		}

	}

	companion object {
		private val matchAny = Regex(".*", RegexOption.DOT_MATCHES_ALL)
	}
}