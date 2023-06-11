package com.benjtissot.sellingmugs

import mui.material.AlertColor
import mui.material.AlertVariant

class AlertState (
    var open: Boolean = false,
    val title: String = "Title",
    val message: String = "Message",
    val severity: AlertColor = AlertColor.info,
    val variant: AlertVariant = AlertVariant.outlined,
) {
}

fun showAlert(
    title: String,
    message: String,
    severity: AlertColor = AlertColor.info,
    variant: AlertVariant = AlertVariant.outlined
): AlertState {
    return AlertState(true, title, message, severity, variant)
}

fun hideAlert(): AlertState {
    return AlertState(false)
}