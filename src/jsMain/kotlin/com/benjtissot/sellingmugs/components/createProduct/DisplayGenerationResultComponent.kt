package com.benjtissot.sellingmugs.components.createProduct

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.forms.GenerateCategoriesForm
import com.benjtissot.sellingmugs.components.forms.GenerateMugsForm
import com.benjtissot.sellingmugs.components.highLevel.CreateTabsComponent
import com.benjtissot.sellingmugs.components.popups.ConfirmCancelButtons
import com.benjtissot.sellingmugs.components.popups.ConfirmCancelPopupProps
import com.benjtissot.sellingmugs.components.popups.ConfirmCheckoutPopupProps
import com.benjtissot.sellingmugs.entities.openAI.CategoriesChatRequestParams
import com.benjtissot.sellingmugs.entities.openAI.CustomStatusCode
import com.benjtissot.sellingmugs.entities.openAI.GenerateCategoryStatus
import com.benjtissot.sellingmugs.entities.openAI.MugsChatRequestParams
import csstype.*
import emotion.react.css
import io.ktor.client.statement.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.lab.TabPanel
import mui.material.Box
import mui.material.Button
import mui.material.Popper
import org.w3c.dom.HTMLButtonElement
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
                    textAlign = TextAlign.center
                    fontNormal()
                    fontWeight = FontWeight.bold
                    height = 10.pct
                    minHeight = 8.vh
                    width = 100.pct
                    marginBottom = 2.pct
                    boxSizing = BoxSizing.borderBox
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    justifyContent = JustifyContent.center
                }
                +props.title
            }

            // Components
            div {
                css {
                    height = 90.pct
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
                                focusedText = statusCode.description
                                target = event.currentTarget
                            }
                            onMouseLeave = {
                                target = null
                                focusedText = ""
                            }
                        }

                    }
                }
            }

        }
    }
}
external interface DisplayCategoriesGenerationResultProps : NavigationProps {
    var list : List<GenerateCategoryStatus>
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

            div {
                css {
                    textAlign = TextAlign.center
                    fontBig()
                    fontWeight = FontWeight.bold
                    height = 10.pct
                    minHeight = 8.vh
                    width = 100.pct
                    marginBottom = 2.pct
                    boxSizing = BoxSizing.borderBox
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    justifyContent = JustifyContent.center
                }
                +props.title
            }

            props.list.forEach { categoryStatus ->
                // Title
                div {
                    css {
                        textAlign = TextAlign.center
                        fontNormal()
                        fontWeight = FontWeight.bold
                        height = 10.pct
                        minHeight = 8.vh
                        width = 100.pct
                        marginBottom = 2.pct
                        boxSizing = BoxSizing.borderBox
                        display = Display.flex
                        flexDirection = FlexDirection.column
                        justifyContent = JustifyContent.center
                    }
                    +if (categoryStatus.message.isEmpty()) {categoryStatus.category.name} else { "${categoryStatus.category.name}: ${categoryStatus.message}" }
                }

                // Components
                div {
                    css {
                        height = "fit-content".unsafeCast<Height>()
                        minHeight = 30.vh
                        maxHeight = 80.vh
                        width = 100.pct
                        boxSizing = BoxSizing.borderBox
                        padding = 1.vw
                        display = Display.flex
                        flexDirection = FlexDirection.column
                        alignItems = AlignItems.center
                        overflowY = "auto".unsafeCast<Overflow>()
                        overflowX = Overflow.hidden
                    }
                    categoryStatus.customStatusCodes.forEach { statusCode ->
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
                                    focusedText = statusCode.description
                                    target = event.currentTarget
                                }
                                onMouseLeave = {
                                    target = null
                                    focusedText = ""
                                }
                            }

                        }
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