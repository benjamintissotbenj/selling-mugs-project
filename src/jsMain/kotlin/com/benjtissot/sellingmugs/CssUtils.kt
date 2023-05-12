package com.benjtissot.sellingmugs

import csstype.*
import emotion.react.css
import react.PropsWithClassName

fun PropsWithClassName.formInputCss(widthValue: Width, backColor: Color?, frontColor: Color?){
    css {
        backColor?.let { backgroundColor = it }
        frontColor?.let { color = it }
        padding = 1.vh
        marginBottom = 1.vh
        width = widthValue
        borderRadius = 2.vh
        boxShadow = BoxShadow(0.px, 0.px, blurRadius = 2.px, spreadRadius = 1.px, NamedColor.gray)
    }
}

fun PropsWithClassName.formInputCss(){
    formInputCss(100.pct, null, null)
}

fun PropsWithClassName.formInputCss(backColor: Color?){
    formInputCss(100.pct, backColor, null)
}