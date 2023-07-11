package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.User
import csstype.*
import emotion.react.css
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.icons.material.PersonOutline
import mui.icons.material.Close
import mui.material.*
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useState


private val LOG = KtorSimpleLogger("UserItem.kt")
external interface UserItemProps: Props {
    var user: User
    var onChangeUserType: (User) -> Unit
    var onDeleteUser: (String) -> Unit
}

val UserItem = FC<UserItemProps> { props ->

    var userType by useState(props.user.userType.toString())

    div {
        css {
            contentCenteredVertically()
            justifySpaceBetween()
            width = 90.pct
            boxSizing = BoxSizing.borderBox
            height = 16.pct
            minHeight = 40.px
            padding = 2.pct
        }
        div {
            css {
                width = 10.pct
            }
            Icon {
                fontSize = IconSize.small
                PersonOutline()
            }
        }

        div {
            css {
                fontNormal()
                width = 60.pct
                boxSizing = BoxSizing.borderBox
                marginLeft = 5.pct
                display = Display.flex
                flexDirection = FlexDirection.column
                alignItems = AlignItems.start
            }
            div {
                +props.user.getNameInitial()
            }
            div {
                css {
                    fontSmall()
                }
                +props.user.email
            }
        }

        Select {
            // Attributes
            css {
                width = 100.rem
                maxWidth = 20.pct
                minWidth = 110.px
                height = 3.rem
                maxHeight = 5.vh
                minHeight = 40.px
                color = NamedColor.black
                fontNormal()
            }
            //labelId = "select-type-label"
            value = userType
            /*label = InputLabel.create{
                id = "select-type-label"
            }*/
            onChange = { event, _ ->
                userType = event.target.value
                LOG.debug("Changing input to ${event.target.value}")
                props.onChangeUserType(props.user.copy(userType = Const.UserType.valueOf(event.target.value)))
            }


            // Children, in the selector

            MenuItem {
                value = Const.UserType.CLIENT.toString()
                +Const.UserType.CLIENT.toString()
            }
            MenuItem {
                value = Const.UserType.ADMIN.toString()
                +Const.UserType.ADMIN.toString()
            }
        }

        IconButton {
            css {
                width = 10.pct
            }
            Close()
            onClick = {props.onDeleteUser(props.user.id)}
        }
    }


}