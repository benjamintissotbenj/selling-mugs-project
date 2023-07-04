package com.benjtissot.sellingmugs.components.buttons

import com.benjtissot.sellingmugs.*
import csstype.VerticalAlign
import csstype.vw
import emotion.react.css
import kotlinx.coroutines.launch
import mui.icons.material.AccountCircle
import mui.icons.material.AccountCircleOutlined
import mui.material.IconButton
import mui.material.IconButtonColor
import mui.material.Size
import react.FC
import react.dom.html.ReactHTML


external interface LoginButtonProps : NavigationProps {
}

val LoginButton = FC<LoginButtonProps> { props ->

    val loggedIn = props.session.jwtToken.isNotEmpty() && (props.session.user != null)

    ReactHTML.div {
        css {
            verticalAlign = VerticalAlign.middle
            marginRight = 2.vw
        }
        IconButton {
            size = Size.small
            color = IconButtonColor.primary
            if (!loggedIn){
                AccountCircle()
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
                AccountCircleOutlined()
                onClick = {
                    scope.launch {
                        recordClick(props.session.clickDataId, Const.ClickType.PROFILE_NAV.toString())
                    }
                    if ((props.session.user?.userType ?: Const.UserType.CLIENT) == Const.UserType.ADMIN){
                        props.navigate.invoke(ADMIN_PANEL_PATH)
                    } else {
                        props.navigate.invoke(USER_INFO_PATH)
                    }
                }
            }

        }
    }
}