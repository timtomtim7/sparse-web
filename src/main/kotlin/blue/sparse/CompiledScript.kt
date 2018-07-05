package blue.sparse

import blue.sparse.script.WebScriptTemplate
import java.io.File

data class CompiledScript(
		val file: File,
		val clazz: Class<out WebScriptTemplate>,
		val timeCompiled: Long = System.currentTimeMillis()
) {
	val needsRecompile: Boolean
		get() = file.lastModified() > timeCompiled
}