package blue.sparse.script.html.styling

import blue.sparse.script.html.styling.components.Color
import kotlinx.html.*
import kotlin.reflect.KProperty

class Style(private val identifier: String? = null) : CssComponent {
    private val substyles = mutableListOf<Style>()
    private val map = mutableMapOf<String, CssComponent>()

    var color: Color? by this
    var background: CssComponentList<Background>? by this

    operator fun String.invoke(body: Style.() -> Unit) {
        substyles += Style(this).apply(body)
    }

    infix fun String.to(value: Any) {
        map[this] = object : CssComponent {
            override val css = value.toString()
        }
    }

    infix fun String.to(value: CssComponent) {
        map[this] = object : CssComponent {
            override val css = value.css
        }
    }

    private inline operator fun <reified T : CssComponent> getValue(thisRef: Any?, property: KProperty<*>): T? {
        return map[property.name] as T?
    }

    operator fun <T : CssComponent> setValue(thisRef: Any?, property: KProperty<*>, value: T?) {
        if (value == null)
            map.remove(property.name)
        else
            map[property.name] = value
    }

    override val css: String
        get() = buildString {

            for (style in substyles)
                append(style.css)

            if (identifier != null)
                append("$identifier {")

            for ((key, value) in map) {
                append("$key: ${value.css};")
            }

            if (identifier != null)
                append("}")
        }
}

fun HtmlHeadTag.styling(identifier: String? = null, body: Style.() -> Unit): Style {
    val styling = Style(identifier).apply(body)
    this.style(StyleType.textCss) {
        unsafe { raw(styling.css) }
    }

    return styling
}

fun CommonAttributeGroupFacade.styling(identifier: String? = null, body: Style.() -> Unit): Style {
    val styling = Style(identifier).apply(body)
    this.style = styling.css
    return styling
}

fun styling(identifier: String? = null, body: Style.() -> Unit): Style {
    return Style(identifier).apply(body)
}