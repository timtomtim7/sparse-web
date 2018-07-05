import kotlinx.html.*
import kotlin.math.*

html {
	head {

	}
	body {
		val step = (1.0 / 200.0) * PI * 2
		for(i in 1..200) {
			span {
				val x = (sin(i * step) * 0.5 + 0.5)
				val y = (cos(i * step) * 0.5 + 0.5)
				val z = (tan(i * step) * 0.5 + 0.5)

				style = """
					position: absolute;
					top: ${x * 90}vh;
					left: ${y * 90}vh;
					color: rgb(${x * 255}, ${y * 255}, ${z * 255});
				""".trimIndent()
				+"█"
			}
		}
	}
}