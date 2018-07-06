package blue.sparse.script.html.styling

interface CssComponent {
    val css: String
}

class CssComponentList<T : CssComponent>(vararg args: T) : CssComponent, MutableList<T> by ArrayList<T>(args.toList()) {
    override val css get() = joinToString(" ", transform = CssComponent::css)
}

interface Background : CssComponent

enum class Position : CssComponent, Background {
    STATIC,
    ABSOLUTE,
    FIXED,
    RELATIVE,
    STICKY,
    INITIAL,
    INHERIT;
    override val css = name.toLowerCase()
}

enum class BackgroundSize : Background {
    AUTO,
    COVER,
    CONTAIN,
    INITIAL,
    INHERIT;

    override val css = name.toLowerCase()
}

enum class BackgroundRepeat : Background {
    REPEAT,
    REPEAT_X,
    REPEAT_Y,
    NO_REPEAT,
    SPACE,
    ROUND,
    INITIAL,
    INHERIT;

    override val css = name.replace("_", "-").toLowerCase()
}

enum class BackgroundClip : Background {
    BORDER_BOX,
    PADDING_BOX,
    CONTENT_BOX;

    override val css = name.replace("_", "-").toLowerCase()
}

