package blue.sparse.script.html.styling.components

import blue.sparse.script.html.styling.Background

data class ImageURL(val url: String) : Background {
    override val css = "url(\"$url\")"
}