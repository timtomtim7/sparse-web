package blue.sparse.script.html.styling.components

import blue.sparse.script.html.styling.CssComponent

abstract class Measurement(private val suffix: String): CssComponent {
    abstract val value: Number
    override val css get() = "$value$suffix"
}

val Number.px get() = Pixel(this)
val Number.percent get() = Percentage(this)
val Number.em get() = EM(this)
val Number.ex get() = EX(this)
val Number.vw get() = ViewWidth(this)
val Number.vh get() = ViewHeight(this)
val Number.vmin get() = ViewMin(this)
val Number.vmax get() = ViewMax(this)
val Number.inch get() = Inch(this)
val Number.cm get() = Centimeter(this)
val Number.mm get() = Millimeter(this)
val Number.pc get() = Pica(this)
val Number.pt get() = Point(this)

class Pixel(override val value: Number) : Measurement("px")
class Percentage(override val value: Number) : Measurement("%")
class EM(override val value: Number) : Measurement("em")
class EX(override val value: Number) : Measurement("ex")
class ViewWidth(override val value: Number) : Measurement("vw")
class ViewHeight(override val value: Number) : Measurement("vh")
class ViewMin(override val value: Number) : Measurement("vmin")
class ViewMax(override val value: Number) : Measurement("vmax")
class Inch(override val value: Number) : Measurement("in")
class Centimeter(override val value: Number) : Measurement("cm")
class Millimeter(override val value: Number) : Measurement("mm")
class Pica(override val value: Number) : Measurement("pc")
class Point(override val value: Number) : Measurement("pt")