package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.entities.local.User
import com.benjtissot.sellingmugs.fontNormalPlus
import csstype.*
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.div


external interface ManageUsersProps: Props {
    var userList: List<User>
    var onChangeUserType: (User) -> Unit
    var onDeleteUser: (String) -> Unit
}

val ManageUsersComponent = FC<ManageUsersProps> { props ->
    div {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
            height = 100.pct
            width = 50.pct
            boxSizing = BoxSizing.borderBox
            paddingLeft = 2.vw
            paddingRight = 2.vw
            overflowY = "auto".unsafeCast<Overflow>()
        }
        div {
            css {
                fontNormalPlus()
                padding = 1.vw
                paddingTop = 2.vw
                width = 100.pct
                boxSizing = BoxSizing.borderBox
            }
            +"User Management"
        }
        props.userList.forEach {
            UserItem {
                user = it
                onChangeUserType = props.onChangeUserType
                onDeleteUser = props.onDeleteUser
            }
        }
    }

}