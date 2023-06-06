package com.benjtissot.sellingmugs

import csstype.*
import emotion.react.css
import react.PropsWithClassName
import com.benjtissot.sellingmugs.Const.ColorCode.*

// CSS for Forms
fun PropsWithClassName.formComponentDivCss(){
    css {
        fullCenterColumnOriented()
        padding = 1.vh
        marginBottom = 1.vh
        marginTop = 10.vh
        width = 50.vw
        maxWidth = 50.rem
        minWidth = 300.px
        boxShade()
    }
}

fun PropertiesBuilder.fullCenterColumnOriented(){
    display = Display.flex
    flexDirection = FlexDirection.column
    alignItems = AlignItems.center
    justifyContent = JustifyContent.center
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
        boxShade()
    }
}


// CSS for general

fun PropsWithClassName.divDefaultCss(){
    css {
        fontNormal()
    }
}

// FONTS

fun PropertiesBuilder.fontSmaller(){
    fontSize = 1.vh
}
fun PropertiesBuilder.fontSmall(){
    fontSize = 1.5.vh
}
fun PropertiesBuilder.fontNormal(){
    fontSize = 2.vh
}
fun PropertiesBuilder.fontBig(){
    fontSize = 3.vh
}
fun PropertiesBuilder.fontBigger(){
    fontSize = 4.vh
}


// BOX SIZE

fun PropertiesBuilder.boxNormalNormal(){
    width = 50.vw
    maxWidth = 50.rem
    minWidth = 300.px
    height = 50.vh
    maxHeight = 50.rem
    minHeight = 200.px
}
fun PropertiesBuilder.boxNormalSmall(){
    width = 50.vw
    maxWidth = 50.rem
    minWidth = 300.px
    height = 20.vh
    maxHeight = 20.rem
    minHeight = 100.px
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

fun PropertiesBuilder.mainPageDiv() {
    height = 88.vh
}


fun PropertiesBuilder.boxShade() {
    borderRadius = 2.vh
    boxShadow = BoxShadow(0.px, 0.px, blurRadius = 2.px, spreadRadius = 1.px, NamedColor.gray)
}
fun PropertiesBuilder.boxBlueShade() {
    borderRadius = 2.vh
    boxShadow = BoxShadow(0.px, 0.px, blurRadius = 2.px, spreadRadius = 1.px, Color(BLUE.code()))
}

fun PropertiesBuilder.center(){
    alignSelf = AlignSelf.center
}

fun PropertiesBuilder.contentCenteredHorizontally(){
    display = Display.flex
    flexDirection = FlexDirection.column
    alignItems = AlignItems.center
}

fun PropertiesBuilder.contentCenteredVertically(){
    display = Display.flex
    flexDirection = FlexDirection.row
    alignItems = AlignItems.center
}
