package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.deleteUser
import com.benjtissot.sellingmugs.entities.User
import com.benjtissot.sellingmugs.fontNormalPlus
import com.benjtissot.sellingmugs.scope
import csstype.*
import emotion.react.css
import kotlinx.coroutines.launch
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
        // todo : fixed width and set columns for the items of different length (eg emails)
        div {
            css {
                fontNormalPlus()
                padding = 1.vw
                paddingTop = 2.vw
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