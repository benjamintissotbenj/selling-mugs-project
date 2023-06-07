package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.entities.User
import csstype.Display
import csstype.Overflow
import csstype.ScrollBehavior
import csstype.rem
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.header


external interface ManageUsersProps: Props {
    var onItemClick: (User) -> Unit
}

val ManageUsersComponent = FC<ManageUsersProps> { props ->



}