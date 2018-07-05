package blue.sparse.script

import com.sun.javafx.util.Utils.clamp
import kotlinx.html.HTML
import kotlinx.html.html
import kotlinx.html.stream.appendHTML
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.script.templates.ScriptTemplateDefinition

@ScriptTemplateDefinition
abstract class WebScriptTemplate(
		val request: HttpServletRequest,
		val response: HttpServletResponse,
		val context: MatchResult
) {

	interface Parameters {
		operator fun get(name: String): String?

		fun optionalInt(name: String): Int? {
			return get(name)?.toIntOrNull()
		}

		fun optionalDouble(name: String): Double? {
			return get(name)?.toDoubleOrNull()
		}

		fun optionalClampedInt(name: String, range: IntRange): Int? {
			return clamp(optionalInt(name) ?: return null, range.start, range.endInclusive)
		}

		fun optionalClampedDouble(name: String, range: ClosedFloatingPointRange<Double>): Double? {
			return clamp(optionalDouble(name) ?: return null, range.start, range.endInclusive)
		}

		fun optionalBoolean(name: String): Boolean? {
			return get(name)?.toBoolean()
		}
	}

	val params = object : Parameters {
		override fun get(name: String): String? {
			return request.getParameterValues(name)?.firstOrNull()
		}
	}

	fun html(block: HTML.() -> Unit) {
		response.contentType = "text/html"
		response.writer.appendHTML().html(block)
	}

	fun error(code: Int = 403): Nothing {
		throw Error(code)
	}

	class Error(val code: Int) : Throwable()

	//xml
	//json
	//html
	//image
	//style
}