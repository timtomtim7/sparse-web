import kotlinx.html.*

html {
	body {
		h1 { +add(5, 8).toString() }
	}
}