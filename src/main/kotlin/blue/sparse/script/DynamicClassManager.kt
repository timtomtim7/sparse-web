package blue.sparse.script

import blue.sparse.util.DirectoryWatchThread
import blue.sparse.web.SparseWeb
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import java.io.File
import java.net.URLClassLoader

object DynamicClassManager {
	private val compiler = K2JVMCompiler()
	var filesChanged = false
		private set

	init {
		DirectoryWatchThread(File("run/src/")) {
			if(it.extension == "kt")
				filesChanged = true
		}
	}

	fun getSourceFiles(folder: File): List<File> {
		return folder
				.walkTopDown()
				.filter { !it.isDirectory && it.extension == "kt" }
				.toList()
	}

	fun compileAll(
			sourceFolder: File,
			target: File = File.createTempFile(
					"sparse-web", ".jar", File("run/temp")
			)
	): DynamicClassLoader {
		filesChanged = false

		val files = getSourceFiles(sourceFolder)
		compiler.exec(System.err,
				"-kotlin-home", "F:/install/kotlinc",
				"-d", target.absolutePath,
				*files.map(File::getAbsolutePath).toTypedArray()
		)
		target.deleteOnExit()

		return DynamicClassLoader(target)
	}

	class DynamicClassLoader(val jar: File): URLClassLoader(arrayOf(jar.toURI().toURL()))
}