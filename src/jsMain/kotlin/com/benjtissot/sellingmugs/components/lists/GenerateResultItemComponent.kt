package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.local.MugCartItem
import com.benjtissot.sellingmugs.entities.openAI.GenerateCategoriesStatus
import com.benjtissot.sellingmugs.entities.printify.order.Order
import com.benjtissot.sellingmugs.entities.printify.toPrettyFormat
import com.benjtissot.sellingmugs.entities.stripe.getCheckoutAmount
import csstype.*
import emotion.react.css
import kotlinx.coroutines.launch
import mui.icons.material.ArrowDropDown
import mui.icons.material.ArrowDropUp
import mui.icons.material.ChevronRight
import mui.lab.LoadingButton
import mui.material.ButtonColor
import mui.material.IconButton
import mui.material.Size
import org.w3c.dom.HTMLButtonElement
import react.FC
import react.Props
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.useState


external interface GenerateResultItemProps: Props {
    var generateCategoriesStatus: GenerateCategoriesStatus
    var onClickShowDetails : () -> Unit
    var index : Int
}

val GenerateResultItemComponent = FC<GenerateResultItemProps> { props ->

    div {
        css {
            card()
            width = 90.pct
            boxSizing = BoxSizing.borderBox
            marginInline = 2.vw
            marginTop = 1.vh
            marginBottom = 1.vh
        }

        // Main info
        div {
            css {
                cardTopHalf()
            }
            // Label
            div {
                css {
                    fontNormal()
                    padding = 16.px
                    boxSizing = BoxSizing.borderBox
                    marginInline = 1.vw
                    fontWeight = FontWeight.bold
                    width = 5.pct
                }
                +"${props.index}"
            }

            // Creation time
            div {
                css {
                    fontNormal()
                    contentCenteredHorizontally()
                    width = 25.pct
                    padding = 16.px
                }
                +"On ${props.generateCategoriesStatus.dateReturned.toPrettyFormat()}"
            }

            // Status
            div {
                css {
                    fontNormal()
                    contentCenteredHorizontally()
                    width = 25.pct
                    padding = 16.px
                    boxSizing = BoxSizing.borderBox
                    marginInline = 2.vw
                    textOverflow = TextOverflow.ellipsis
                    overflow = Overflow.hidden
                }
                +props.generateCategoriesStatus.message
            }

            // Show Details
            IconButton {
                css {
                    display = Display.flex
                    flexDirection = FlexDirection.row
                    alignItems = AlignItems.center
                    width = 25.pct
                }
                size = Size.small
                div {
                    css {
                        marginRight = 1.vw
                    }
                    +"Show details"
                }
                ChevronRight()
                onClick = {
                    props.onClickShowDetails()
                }
            }
        }

    }
}