package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.User
import csstype.*
import emotion.react.css
import io.ktor.util.logging.*
import mui.icons.material.PersonOutline
import mui.material.*
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.useState


private val LOG = KtorSimpleLogger("UserItem.kt")
external interface UserItemProps: Props {
    var user: User
    var onChangeUserType: (User) -> Unit
}

val UserItem = FC<UserItemProps> {
        props ->
    div {
        css {
            contentCenteredVertically()
            justifySpaceBetween()
            width = 40.vw
            height = 10.vh
            padding = 1.rem
        }

        Icon {
            fontSize = IconSize.small
            PersonOutline()
        }

        div {
            css {
                fontNormal()
            }
            +props.user.getNameInitial()
        }

        var userType by useState(props.user.userType.toString())



        Select {
            // Attributes
            css {
                width = 100.rem
                maxWidth = 10.vw
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
    }


}