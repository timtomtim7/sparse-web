package blue.sparse.script.html.styling.components

import blue.sparse.math.vector.floats.*
import blue.sparse.script.html.styling.Background

data class Color(var r: Float, var g: Float, var b: Float, var a: Float = 1f) : Background {
    constructor(vec3: Vector3f, a: Float = 1f) : this(vec3.x, vec3.y, vec3.z, a)
    constructor(vec4: Vector4f) : this(vec4.x, vec4.y, vec4.z, vec4.w)
    constructor(hex: Int) : this(hex.vectorFromIntRGBA())

    override val css get() = "#$hex"

    val hex
        get() = java.lang.Long.toUnsignedString(vec4f(r, g, b, a).toIntRGBA().toLong() and 0xffffffff, 16).padStart(8, '0')

    companion object {
        fun Int.vectorFromIntRGBA(): Vector4f {
            val r = (this shr 24) and 0xFF
            val g = (this shr 16) and 0xFF
            val b = (this shr 8) and 0xFF
            val a = (this) and 0xFF

            return vec4f(r.toFloat(), g.toFloat(), b.toFloat(), a.toFloat()) / 255.0f
        }

        fun Vector4f.toIntRGBA(): Int {
            val vectorRGB = round(clamp(this, 0f, 1f) * 255f)
            return (vectorRGB.w.toInt()) or (vectorRGB.x.toInt() shl 24) or (vectorRGB.y.toInt() shl 16) or (vectorRGB.z.toInt() shl 8)
        }

        fun Vector3f.toIntRGB(): Int {
            val vectorRGB = round(clamp(this, 0f, 1f) * 255f)
            return (vectorRGB.x.toInt() shl 16) or (vectorRGB.y.toInt() shl 8) or vectorRGB.z.toInt()
        }

        private fun fromHex(hex: Int): Color {
            return Color(0f, 0f, 0f, 0f)
        }

        fun Int.vectorFromIntRGB(): Vector3f {
            val r = (this shr 16) and 0xFF
            val g = (this shr 8) and 0xFF
            val b = this and 0xFF

            return vec3f(r.toFloat(), g.toFloat(), b.toFloat()) / 255.0f
        }

        fun Int.vectorFromIntARGB(): Vector4f {
            val a = (this shr 24) and 0xFF
            val r = (this shr 16) and 0xFF
            val g = (this shr 8) and 0xFF
            val b = this and 0xFF

            return vec4f(r.toFloat(), g.toFloat(), b.toFloat(), a.toFloat()) / 255.0f
        }

        fun Vector4f.toIntARGB(): Int {
            val vectorRGB = round(clamp(this, 0f, 1f) * 255f)
            return (vectorRGB.w.toInt() shl 24) or (vectorRGB.x.toInt() shl 16) or (vectorRGB.y.toInt() shl 8) or vectorRGB.z.toInt()
        }

        fun Vector3f.RGBtoHexString() = toIntRGB().toString(16).toUpperCase().padStart(6, '0')

        fun Vector3f.RGBtoIntString() = joinToString { Math.round(it * 255.0f).toString() }

        fun Vector3f.HSBtoString() = String.format("%.1f, %.1f, %.1f", x * 360.0f, y * 100.0f, z * 100.0f)

        fun Vector3f.RGBtoHSB(): Vector3f {
            val max = max(this)
            val min = min(this)
            val delta = max - min

            var h = 0.0f
            val s = if (max == 0f) 0f else delta / max
            val b = max

            if (delta != 0.0f) {
                when {
                    x == max -> h = (y - z) / delta
                    y == max -> h = 2 + (z - x) / delta
                    z == max -> h = 4 + (x - y) / delta
                }
                h /= 6.0f
                if (h > 1.0f) h -= 1.0f
                if (h < 0.0f) h += 1.0f
            }

            return vec3f(h, s, b)
        }

        fun Vector3f.HSBtoRGB(): Vector3f {
            if (y == 0.0f) return vec3f(z)

            val h = x * 6
            val i = h.toInt()
            val f = h - i
            val p = z * (1 - y)
            val q = z * (1 - y * f)
            val t = z * (1 - y * (1 - f))

            return when (i) {
                0 -> vec3f(z, t, p)
                1 -> vec3f(q, z, p)
                2 -> vec3f(p, z, t)
                3 -> vec3f(p, q, z)
                4 -> vec3f(t, p, z)
                else -> vec3f(z, p, q)
            }
        }
    }
}