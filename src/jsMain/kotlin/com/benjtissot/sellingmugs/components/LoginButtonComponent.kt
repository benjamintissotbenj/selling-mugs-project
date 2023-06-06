package com.benjtissot.sellingmugs.components

import com.benjtissot.sellingmugs.LOGIN_PATH
import com.benjtissot.sellingmugs.NavigationProps
import csstype.VerticalAlign
import csstype.vw
import emotion.react.css
import mui.icons.material.Person
import mui.icons.material.PersonOutline
import mui.material.IconButton
import mui.material.IconButtonColor
import mui.material.Size
import react.FC
import react.dom.html.ReactHTML


external interface LoginButtonProps : NavigationProps {
    var loggedIn: Boolean
}

val LoginButton = FC<LoginButtonProps> { props ->

    ReactHTML.div {
        css {
            verticalAlign = VerticalAlign.middle
            marginRight = 2.vw
        }
        IconButton {
            size = Size.small
            color = IconButtonColor.primary
            if (!props.loggedIn){
                Person()
                onClick = {
                    props.navigate.invoke(LOGIN_PATH)
                }
            } else {
                ReactHTML.div {
                    css {
                        marginRight = 1.vw
                    }
                    +props.session.user!!.getNameInitial()
                }
                PersonOutline()
                onClick = {
                    props.navigate.invoke(LOGIN_PATH)
                }
            }

        }
    }
}