import blue.sparse.script.WebScriptTemplate
import kotlinx.html.HTML

val params: WebScriptTemplate.Parameters
	get() = TODO()

fun html(block: HTML.() -> Unit): Unit = TODO()

inline fun <reified T : Any> json(value: T): Unit = TODO()

fun error(code: Int = 403): Nothing = TODO()
