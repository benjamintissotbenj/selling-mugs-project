package com.benjtissot.sellingmugs.components.createProduct

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.openAI.*
import csstype.*
import emotion.react.css
import io.ktor.util.logging.*
import mui.material.Box
import mui.material.LinearProgress
import mui.material.LinearProgressVariant
import mui.material.Popper
import org.w3c.dom.HTMLDivElement
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useState


private val LOG = KtorSimpleLogger("DisplayGenerationResultComponent.kt")

external interface DisplayGenerationResultProps : NavigationProps {
    var list : List<CustomStatusCode>
    var title : String
}

val DisplayGenerationResultComponent = FC<DisplayGenerationResultProps> { props ->
    var focusedText by useState("")
    var target : HTMLDivElement? by useState(null)

    // Display details on the long messages
    TextPopup {
        popupTarget = target
        text = focusedText
    }

    // Parent to hold flex to center the box
    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            height = "fit-content".unsafeCast<Height>()
            maxHeight = 90.pct
            width = 50.pct
            boxSizing = BoxSizing.borderBox
            paddingLeft = 1.vw
            paddingRight = 1.vw
            margin = 2.vw
        }

        // Box
        div {
            css {
                fontNormal()
                boxNormalNormal()
                boxShade()
                display = Display.flex
                flexDirection = FlexDirection.column
                alignItems = AlignItems.center
                height = 100.pct
                width = 100.pct
                paddingInline = 2.vw
                paddingBottom = 2.vh
            }

            // Title
            div {
                css {
                    componentTitle()
                }
                +props.title
            }

            // Components
            CustomStatusCodeListComponent {
                list = props.list
                height = if (props.list.isNotEmpty()) 90.pct else 0.pct
                onMouseEnterMessage = { div, text ->
                    focusedText = text
                    target = div
                }
                onMouseLeaveMessage = {
                    target = null
                    focusedText = ""
                }
            }

        }
    }
}

external interface DisplayCategoriesGenerationResultProps : NavigationProps {
    var status : GenerateCategoriesStatus
    var title : String

}


val DisplayCategoriesGenerationResultComponent = FC<DisplayCategoriesGenerationResultProps> { props ->
    var focusedText by useState("")
    var target : HTMLDivElement? by useState(null)

    // Display details on the long messages
    TextPopup {
        popupTarget = target
        text = focusedText
    }

    // Parent to hold flex to center the box
    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            height = "fit-content".unsafeCast<Height>()
            maxHeight = 90.pct
            width = 50.pct
            boxSizing = BoxSizing.borderBox
            paddingLeft = 1.vw
            paddingRight = 1.vw
            margin = 2.vw
        }

        // Box
        div {
            css {
                overflowY = "auto".unsafeCast<Overflow>()
                fontNormal()
                boxNormalNormal()
                boxShade()
                display = Display.flex
                flexDirection = FlexDirection.column
                alignItems = AlignItems.center
                height = 100.pct
                width = 100.pct
                paddingInline = 2.vw
                paddingBottom = 2.vh
            }

            // Title
            div {
                css {
                    componentTitle()
                }
                +props.title
            }
            div {
                css {
                    componentTitle()
                    fontWeight = FontWeight.normal
                }
                +props.status.message
            }
            div {
                css {
                    width = 100.pct
                    paddingInline = 5.pct
                    boxSizing = BoxSizing.borderBox
                    display = Display.flex
                    flexDirection = FlexDirection.rowReverse
                    alignItems = AlignItems.center
                    fontNormal()
                }
                div {
                    css {
                        width = "fit-content".unsafeCast<Width>()
                    }
                    +"${props.status.calculateCompletionPercentage()}%"
                }
                LinearProgress {
                    css {
                        width = 90.pct
                        marginRight = 5.pct
                        boxSizing = BoxSizing.borderBox
                    }
                    variant = LinearProgressVariant.buffer
                    valueBuffer = props.status.calculateCompletionPercentage()
                    value = props.status.calculateSuccessPercentage()
                }
            }

            props.status.statuses.forEach { categoryStatus ->
                // Title
                div {
                    css {
                        componentTitle()
                    }
                    +if (categoryStatus.message.isEmpty()) {categoryStatus.category.name} else { "${categoryStatus.category.name}: ${categoryStatus.message}" }
                }

                // Components
                CustomStatusCodeListComponent {
                    list = categoryStatus.customStatusCodes
                    height = "fit-content".unsafeCast<Height>()
                    minHeight = 20.vh
                    maxHeight = 80.vh
                    onMouseEnterMessage = { div, text ->
                        focusedText = text
                        target = div
                    }
                    onMouseLeaveMessage = {
                        target = null
                        focusedText = ""
                    }
                }
            }

        }
    }
}


external interface CustomStatusCodeListProps: Props {
    var list: List<CustomStatusCode>
    var onMouseEnterMessage: (HTMLDivElement, String) -> Unit
    var onMouseLeaveMessage: () -> Unit
    var height: Height
    var minHeight: MinHeight?
    var maxHeight: MaxHeight?
}

/**
 * Helps display a list of StatusCodes, refactored code
 */
val CustomStatusCodeListComponent = FC<CustomStatusCodeListProps> { props ->
    div {
        css {
            height = props.height
            props.minHeight?.let {minHeight = it}
            props.maxHeight?.let {maxHeight = it}
            width = 100.pct
            boxSizing = BoxSizing.borderBox
            padding = 1.vw
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
            overflowY = "auto".unsafeCast<Overflow>()
            overflowX = Overflow.hidden
        }
        props.list.forEach { statusCode ->
            val color = if (statusCode.value == 200) { Const.ColorCode.BLUE.code() } else { Const.ColorCode.RED.code() }
            div {
                css {
                    width = 100.pct
                    padding = 2.vh
                    margin = 1.vh
                    boxSizing = BoxSizing.borderBox
                    height = 5.pct
                    display = Display.flex
                    flexDirection = FlexDirection.row
                    alignItems = AlignItems.center
                    justifyContent = JustifyContent.spaceBetween
                    borderRadius = 2.vh
                    boxShadow = BoxShadow(0.px, 0.px, blurRadius = 2.px, spreadRadius = 1.px, Color(color))
                }
                div {
                    css {
                        marginRight = 2.vw
                    }
                    +"Status ${statusCode.value}"
                }

                div {
                    css {
                        width = "fit-content".unsafeCast<Width>()
                        maxWidth = 70.pct
                        textOverflow = TextOverflow.ellipsis
                        overflow = Overflow.hidden
                        whiteSpace = WhiteSpace.nowrap
                        marginLeft = 2.vw
                    }
                    +statusCode.description
                    onMouseEnter = { event ->
                        props.onMouseEnterMessage(event.currentTarget, statusCode.description)
                    }
                    onMouseLeave = {
                        props.onMouseLeaveMessage()
                    }
                }

            }
        }
    }
}

external interface TextPopupProps: Props {
    var popupTarget : HTMLDivElement?
    var text: String
}

val TextPopup = FC<TextPopupProps> { props ->

    Popper {
        css {
            border = 2.px
            borderColor = Color(Const.ColorCode.BACKGROUND_GREY_DARKEST.code())
            height = "fit-content".unsafeCast<Height>()
        }
        open = (props.popupTarget != null)
        anchorEl = props.popupTarget
        Box {
            css {
                paddingTop = 1.vw
                paddingBottom = 1.vw
                paddingLeft = 2.vw
                paddingRight = 2.vw
                boxSizing = BoxSizing.borderBox
                width = 25.vw
                height = "fit-content".unsafeCast<Height>()
                backgroundColor = Color(Const.ColorCode.BACKGROUND_GREY_EVEN_DARKER.code())
                overflowWrap = OverflowWrap.anywhere
            }
            +props.text
        }
    }
}