package blue.sparse.util

import java.io.File
import java.net.URL
import java.net.URLClassLoader

private val validClasspathFilesExtensions = setOf("jar", "zip", "java")

private fun URL.toFile(): File? {
	return try {
		File(toURI().schemeSpecificPart)
	} catch (e: java.net.URISyntaxException) {
		if (protocol != "file") null
		else File(file)
	}
}

internal fun ClassLoader.classpath(): List<File> {
	return generateSequence(this) { it.parent }.toList().flatMap {
		(it as? URLClassLoader)?.urLs?.mapNotNull {
			it.toFile()?.takeIf { file -> file.isDirectory || validClasspathFilesExtensions.any { file.extension == it } }
		} ?: emptyList()
	}
}

internal fun currentClasspath() = Thread.currentThread().contextClassLoader.classpath()