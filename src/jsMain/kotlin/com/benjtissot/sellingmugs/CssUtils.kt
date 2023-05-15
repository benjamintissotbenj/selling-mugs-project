package com.benjtissot.sellingmugs

import csstype.*
import emotion.react.css
import react.PropsWithClassName

// CSS for Forms
fun PropsWithClassName.formComponentDivCss(){
    css {
        display = Display.flex
        flexDirection = FlexDirection.column
        alignItems = AlignItems.center
        padding = 1.vh
        marginBottom = 1.vh
        width = 50.vw
        maxWidth = 50.rem
        minWidth = 300.px
        borderRadius = 2.vh
        boxShadow = BoxShadow(0.px, 0.px, blurRadius = 2.px, spreadRadius = 1.px, NamedColor.gray)
    }
}

fun PropsWithClassName.formCss(){
    css {
        display = Display.flex
        flexDirection = FlexDirection.column
        alignItems = AlignItems.center
        padding = 1.vh
        marginBottom = 1.vh
    }
}
fun PropsWithClassName.formLabelGroupDivCss(){
    css {
        display = Display.flex
        flexDirection = FlexDirection.column
        alignItems = AlignItems.start
        padding = 1.vh
    }
}
fun PropsWithClassName.formLabelCss(){
    css {
        fontSize = 2.vh
        marginBottom = 1.vh
        maxWidth = 50.rem
        width = 20.vw
        minWidth = 180.px
    }
}

fun PropsWithClassName.formInputCss(){
    formInputCss(100.pct, 180.px, null, null)
}

fun PropsWithClassName.formInputCss(backColor: Color?){
    formInputCss(100.pct, 180.px, backColor, null)
}

fun PropsWithClassName.formInputCss(widthValue: Width, minimumWidth: MinWidth, backColor: Color?, frontColor: Color?){
    css {
        backColor?.let { backgroundColor = it }
        frontColor?.let { color = it }
        padding = 1.vh
        marginBottom = 1.vh
        width = widthValue
        minWidth = minimumWidth
        fontSize = 2.vh
        borderRadius = 2.vh
        boxShadow = BoxShadow(0.px, 0.px, blurRadius = 2.px, spreadRadius = 1.px, NamedColor.gray)
    }
}


// CSS for general

fun PropsWithClassName.divDefaultCss(){
    css {
        fontSize = 2.vh
    }
}

fun PropertiesBuilder.divDefaultHorizontalCss(){
        display = Display.flex
        flexDirection = FlexDirection.row
        alignItems = AlignItems.start
        padding = 1.vh
        fontSize = 2.vh
}

fun PropertiesBuilder.justifySpaceBetween() {
    display = Display.flex
    justifyContent = JustifyContent.spaceBetween
}

