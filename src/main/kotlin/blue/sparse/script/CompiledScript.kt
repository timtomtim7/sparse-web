package blue.sparse.script

import java.io.File

data class CompiledScript(
		val file: File,
		val clazz: Class<*>,
		val timeCompiled: Long = System.currentTimeMillis()
) {

	val needsRecompile: Boolean
		get() = file.lastModified() > timeCompiled
}