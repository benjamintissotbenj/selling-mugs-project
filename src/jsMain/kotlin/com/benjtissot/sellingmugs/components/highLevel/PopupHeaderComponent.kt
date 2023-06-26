package com.benjtissot.sellingmugs.components.highLevel

import com.benjtissot.sellingmugs.NavigationProps
import com.benjtissot.sellingmugs.contentCenteredVertically
import com.benjtissot.sellingmugs.fontBig
import csstype.*
import emotion.react.css
import io.ktor.util.logging.*
import mui.icons.material.CloseRounded
import mui.material.IconButton
import mui.material.Size
import react.FC
import react.dom.html.ReactHTML.div


private val LOG = KtorSimpleLogger("PopupHeaderComponent.kt")

external interface PopupHeaderProps : NavigationProps {
    var onClickClose: () -> Unit
    var title: String
}

val PopupHeaderComponent = FC<PopupHeaderProps> { props ->
    // Header container
    div {
        css {
            display = Display.grid
            gridTemplateColumns = "1fr repeat(3, auto) 1fr".unsafeCast<GridTemplateColumns>()
            justifyItems = JustifyItems.center
            marginBottom = 1.vh
        }

        // Close button
        IconButton {
            css {
                marginRight = "auto".unsafeCast<MarginRight>()
            }
            CloseRounded()
            size = Size.small
            onClick = {props.onClickClose()}
        }

        div {
            css {
                fontBig()
                contentCenteredVertically()
            }
            +props.title
        }
    }
}
