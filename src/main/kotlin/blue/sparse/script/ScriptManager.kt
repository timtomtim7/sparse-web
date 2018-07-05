package blue.sparse.script

import blue.sparse.util.currentClasspath
import com.intellij.openapi.Disposable
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.*
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.config.*
import org.jetbrains.kotlin.script.KotlinScriptDefinition
import java.io.File
import kotlin.reflect.KClass

class ScriptManager {

	var parentClassLoader: ClassLoader? = null

	private val classPath = ArrayList<File>()
	private val cache = HashMap<File, CompiledScript>()

	fun clearClassPath() {
		classPath.clear()
	}

	fun addClassPathFile(file: File) {
		classPath.add(file)
		cache.clear()
	}

	fun removeClassPathFile(file: File) {
		classPath.remove(file)
		cache.clear()
	}

	init {
		System.setProperty("idea.io.use.fallback", "true")
	}

	@Suppress("UNCHECKED_CAST")
	operator fun get(file: File, template: KClass<*> = WebScriptTemplate::class): CompiledScript {
		val cached = cache[file]
		if(cached != null && !cached.needsRecompile)
			return cached

		val env = KotlinCoreEnvironment.createForProduction(
				Disposable { },
				createConfig(file, template),
				EnvironmentConfigFiles.JVM_CONFIG_FILES
		)

		val result = KotlinToJVMBytecodeCompiler.compileScript(env, parentClassLoader ?: javaClass.classLoader)
				?: throw IllegalStateException("Failed to compile script")

		val compiled = CompiledScript(file, result)
		cache[file] = compiled
		return compiled
	}

	private fun createConfig(script: File, template: KClass<*>): CompilerConfiguration {
		val configuration = CompilerConfiguration()
		configuration.addKotlinSourceRoot(script.absolutePath)
		configuration.addJvmClasspathRoots(currentClasspath())
		configuration.addJvmClasspathRoots(classPath)

		configuration.put(JVMConfigurationKeys.RETAIN_OUTPUT_IN_MEMORY, false)
		configuration.put(JVMConfigurationKeys.DISABLE_STANDARD_SCRIPT_DEFINITION, true)
		configuration.put(
				CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
				PrintingMessageCollector(System.err, MessageRenderer.PLAIN_RELATIVE_PATHS, true)
		)
		configuration.put(
				JVMConfigurationKeys.SCRIPT_DEFINITIONS,
				listOf(KotlinScriptDefinition(template))
		)

		return configuration
	}

}
