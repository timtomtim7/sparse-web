package blue.sparse.util

import com.sun.nio.file.ExtendedWatchEventModifier
import java.io.File
import java.lang.ref.WeakReference
import java.nio.file.*

class DirectoryWatchThread(val dir: File, callback: (File) -> Unit) : Thread("DirectoryWatchThread") {

	private val callback = WeakReference(callback)
	private var running = false

	private val watchService: WatchService

	init {
		if (!dir.exists())
			throw IllegalArgumentException("File does not exist")

		watchService = FileSystems.getDefault().newWatchService()
		watchRecursively(dir)

		isDaemon = true
		start()
	}

	private fun watchRecursively(file: File) {
		if (!file.isDirectory)
			return

		val path = file.absoluteFile.toPath()
		path.register(
				watchService,
				arrayOf(
						StandardWatchEventKinds.ENTRY_MODIFY,
						StandardWatchEventKinds.ENTRY_CREATE,
						StandardWatchEventKinds.ENTRY_DELETE
				),
				ExtendedWatchEventModifier.FILE_TREE

		)

		file.listFiles().forEach(this::watchRecursively)
	}

	fun stopWatching() {
		running = false
	}

	override fun run() {
		running = true
		while (running) {
			val key = watchService.take()
			Thread.sleep(100L)

			val events = key.pollEvents()
			for (event in events) {
				val relative = event.context() as? Path ?: continue
				val changedFile = dir.toPath().resolve(relative).toFile()

				if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE && changedFile.isDirectory) {
					watchRecursively(changedFile)
				}

				val callback = callback.get()
				if (callback == null) {
					running = false
					break
				}

				callback(changedFile)
			}

			key.reset()
		}
		watchService.close()
	}

}