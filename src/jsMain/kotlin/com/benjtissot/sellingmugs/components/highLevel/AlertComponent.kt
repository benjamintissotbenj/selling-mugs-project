package com.benjtissot.sellingmugs.components.highLevel

import com.benjtissot.sellingmugs.AlertState
import com.benjtissot.sellingmugs.hideAlert
import csstype.Cursor
import emotion.react.css
import mui.material.Alert
import mui.material.AlertTitle
import mui.material.Snackbar
import mui.material.SnackbarCloseReason
import react.FC
import react.Props
import react.StateInstance


external interface AlertProps : Props {
    var alertState: AlertState
    var setAlert: (AlertState) -> Unit
}


val AlertComponent = FC <AlertProps> { props ->
    val alertState = props.alertState
    if (alertState.open){
        Snackbar {
            css {
                cursor = Cursor.pointer
            }
            this.open = true
            autoHideDuration = 5000
            onClose = { _, reason ->
                if (reason == SnackbarCloseReason.timeout && !alertState.stayOn){
                } else {
                    props.setAlert(hideAlert())
                }
            }
            onClick = {
                props.setAlert(hideAlert())
            }
            Alert {
                severity = alertState.severity
                variant = alertState.variant
                AlertTitle {
                    +(alertState.title)
                }
                +(alertState.message)
            }
        }
    }
}