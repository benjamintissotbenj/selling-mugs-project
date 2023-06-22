package com.benjtissot.sellingmugs.components.highLevel

import com.benjtissot.sellingmugs.AlertState
import com.benjtissot.sellingmugs.hideAlert
import csstype.pct
import csstype.vh
import emotion.react.css
import mui.material.Alert
import mui.material.AlertTitle
import mui.material.Snackbar
import mui.material.SnackbarCloseReason
import react.*
import react.dom.html.ReactHTML.div


external interface CreateTabsProps : PropsWithChildren {
    override var children : ReactNode?
    var index : String
    var value: String
}


val CreateTabsComponent = FC <CreateTabsProps> { props ->
    div {
        css {
            height = 90.vh
            width = 100.pct
        }
        hidden = props.value != props.index
        id = "simple-tab-${props.index}"
            children
    }
}

