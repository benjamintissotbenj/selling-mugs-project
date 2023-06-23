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
}
