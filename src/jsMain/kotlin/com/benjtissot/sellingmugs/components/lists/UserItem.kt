package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.contentCenteredVertically
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.entities.User
import com.benjtissot.sellingmugs.fontNormal
import com.benjtissot.sellingmugs.fontSmall
import com.benjtissot.sellingmugs.justifySpaceBetween
import csstype.AlignContent
import csstype.rem
import emotion.react.css
import mui.icons.material.PersonOutline
import mui.material.Icon
import mui.material.IconSize
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img


external interface UserItemProps: Props {
    var user: User
    var onItemClick: (User) -> Unit
}

val UserItem = FC<UserItemProps> {
        props ->
    div {
        css {
            contentCenteredVertically()
            justifySpaceBetween()
            width = 10.rem
            height = 10.rem
            padding = 1.rem
        }

        Icon {
            fontSize = IconSize.small
            PersonOutline()
        }

        div {
            css {
                fontSmall()
            }
            +props.user.getNameInitial()
        }

        onClick = {props.onItemClick(props.user)}
    }


}