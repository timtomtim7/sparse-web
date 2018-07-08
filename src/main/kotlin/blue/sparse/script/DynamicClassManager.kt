package blue.sparse.script

import blue.sparse.util.DirectoryWatchThread
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import java.io.File
import java.net.URLClassLoader

object DynamicClassManager {
	private val compiler = K2JVMCompiler()
	var filesChanged = false
		private set

	init {
		DirectoryWatchThread(File("run/src/")) {
			if (it.extension == "kt")
				filesChanged = true
		}
	}

	fun getSourceFiles(folder: File): List<File> {
		return folder
				.walkTopDown()
				.filter {
					!it.isDirectory
							&& it.extension == "kt"
							&& it.nameWithoutExtension != "DSL"
				}
				.toList()
	}

	fun compileAll(
			sourceFolder: File,
			target: File? = null
	): DynamicClassLoader? {
		val realTarget = target ?: File.createTempFile(
				"sparse-web", ".jar", File("run/temp")
		)
		realTarget.absoluteFile.parentFile.mkdirs()

		filesChanged = false

		val files = getSourceFiles(sourceFolder)
		if (files.isEmpty())
			return null
		compiler.exec(System.err,
				"-kotlin-home", System.getProperty("KOTLIN_HOME"),
				"-d", realTarget.absolutePath,
				*files.map(File::getAbsolutePath).toTypedArray()
		)
		realTarget.deleteOnExit()

		return DynamicClassLoader(realTarget)
	}

	class DynamicClassLoader(val jar: File) : URLClassLoader(arrayOf(jar.toURI().toURL()))
}