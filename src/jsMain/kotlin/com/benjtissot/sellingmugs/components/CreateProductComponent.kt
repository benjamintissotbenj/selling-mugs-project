package com.benjtissot.sellingmugs.components

import com.benjtissot.sellingmugs.*
import csstype.Display
import csstype.FlexDirection
import csstype.JustifyContent
import csstype.pct
import emotion.react.css
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.icons.material.Person
import mui.icons.material.PersonRemove
import mui.material.IconButton
import react.FC
import react.dom.html.ReactHTML
import react.dom.html.ReactHTML.div


private val LOG = KtorSimpleLogger("CreateProductComponent.kt")

external interface CreateProductProps : NavigationProps {
}

val CreateProductComponent = FC<CreateProductProps> { props ->
    // Parent to hold flex to center the box
    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            width = 100.pct
        }

        div {
            css {
                fontNormal()
                boxNormal()
                center()
                justifyContent = JustifyContent.center
            }



        }
    }
}
