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
            gridTemplateColumns = "repeat(3, 1fr)".unsafeCast<GridTemplateColumns>()
            justifyItems = JustifyItems.center
            justifyContent = JustifyContent.right
            marginBottom = 1.vh
            width = 100.pct
        }

        div {+""}

        div {
            css {
                fontBig()
                contentCenteredVertically()
            }
            +props.title
        }

        // Close button

        div {
            css {
                display = Display.flex
                flexDirection = FlexDirection.rowReverse
                marginLeft = "auto".unsafeCast<MarginLeft>()
            }
            IconButton {
                CloseRounded()
                size = Size.small
                onClick = {props.onClickClose()}
            }
        }

    }
}
