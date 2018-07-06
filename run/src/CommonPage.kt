import blue.sparse.script.html.styling.components.Color
import blue.sparse.script.html.styling.components.ImageURL
import blue.sparse.script.html.styling.components.percent
import blue.sparse.script.html.styling.components.px
import blue.sparse.script.html.styling.styling
import kotlinx.html.*

val commonStyle = styling {

    "*" {
        "text-indent" to 20.px
        "font-family" to "'Ubuntu', sans-serif;"
        "color" to Color(0x183c6aff)
    }

    "body" {
        "background" to ImageURL(randomTriangles("000000", "0f0f0f", random = 1f))
        "width" to 100.percent
        "padding" to 0.px
        "margin" to 0.px
    }

    "#header" {
        "background" to ImageURL(randomTriangles("000000", "090909"))
        "top" to 0
        "width" to 100.percent
        "display" to "block"
        "box-shadow" to "0px 5px 5px rgba(0,0,0,0.2)"
        "z-index" to 100
        "margin" to "0 auto"
        "padding" to "1vh 0 1vh 0"
    }

    "#header *" {
        "display" to "inline-block"
        "margin" to "0 auto"
        "padding" to "0"
        "vertical-align" to "middle"
        "text-indent" to "0"
    }

    "#header h2" {
        "font-size" to "4em"
        "font-weight" to "bold"
        "margin-bottom" to "0.5%"
        "letter-spacing" to "2px"
        "text-shadow" to "3px 3px 3px rgba(0,0,50,0.6)"
    }

    "#header img" {
        "width" to 100.px
        "height" to 100.px
    }
}

fun HTML.common(body: BODY.() -> Unit) {
    head {
        title("Paradox")
        styleLink("https://fonts.googleapis.com/css?family=Ubuntu")
        style = commonStyle.css
    }

    body {
        div {
            id = "header"
            img(src = "images/paradoxblue.png")
            h2 { +"Paradox" }
        }

        apply(body)
    }
}

fun randomTriangles(minColor: String = "000000", maxColor: String = "111111", res: Int = 1024, random: Float = 1f): String {
    return "https://sparse.blue/api/images/triangles.kts?colorMin=$minColor&colorMax=$maxColor&random=$random&res=$res"
}