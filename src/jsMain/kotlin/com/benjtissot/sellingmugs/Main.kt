package com.benjtissot.sellingmugs

import kotlinx.browser.document
import mui.material.PaletteMode
import mui.material.styles.*
import mui.system.ThemeOptions
import mui.system.ThemeProvider
import mui.system.createTheme
import react.create
import react.dom.client.createRoot

fun main() {
    val container = document.getElementById("root") ?: error("Couldn't find container!")
    createRoot(container).render(App.create())
    // TODO : change title/h1 for Search Engine Optimisation
    // TODO : search for sitemap details + generate a map of all URLs
    // TODO : change url from product id to name something
    // TODO : google search central
}
