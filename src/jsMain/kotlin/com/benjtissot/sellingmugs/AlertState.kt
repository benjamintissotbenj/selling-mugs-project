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

/**
 * Default creation for an Alert
 */
fun showAlert(
    title: String,
    message: String,
    severity: AlertColor = AlertColor.info,
    variant: AlertVariant = AlertVariant.outlined
): AlertState {
    return AlertState(true, title, message, severity, variant)
}

/**
 * Creation of an success Alert
 */
fun successAlert(
    message: String,
    title: String = "Success"
): AlertState {
    return showAlert(title, message, AlertColor.success)
}

/**
 * Creation of an error Alert
 */
fun errorAlert(
    message: String,
    title: String = "Error"
): AlertState {
    return showAlert(title, message, AlertColor.error)
}

/**
 * Creation of a warning Alert
 */
fun warningAlert(
    message: String,
    title: String = "Warning"
): AlertState {
    return showAlert(title, message, AlertColor.warning)
}

/**
 * Creation of an info Alert
 */
fun infoAlert(
    message: String,
    title: String = "Information"
): AlertState {
    return showAlert(title, message, AlertColor.info)
}

/**
 * Creation for an empty Alert
 */
fun hideAlert(): AlertState {
    return AlertState(false)
}