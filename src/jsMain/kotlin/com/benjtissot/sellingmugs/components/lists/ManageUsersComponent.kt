package com.benjtissot.sellingmugs.components.lists

import com.benjtissot.sellingmugs.entities.User
import react.FC
import react.Props


external interface ManageUsersProps: Props {
    var userList: List<User>
    var onChangeUserType: (User) -> Unit
}

val ManageUsersComponent = FC<ManageUsersProps> { props ->

    props.userList.forEach {
        UserItem {
            user = it
            onChangeUserType = props.onChangeUserType
        }
    }

}