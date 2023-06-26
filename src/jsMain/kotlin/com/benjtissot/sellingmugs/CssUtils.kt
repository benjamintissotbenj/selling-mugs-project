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
    fontSize = 0.5.rem
}
fun PropertiesBuilder.fontSmall(){
    fontSize = 0.75.rem
}
fun PropertiesBuilder.fontNormal(){
    fontSize = 1.rem
}
fun PropertiesBuilder.fontNormalPlus(){
    fontSize = 1.25.rem
}
fun PropertiesBuilder.fontBig(){
    fontSize = 1.5.rem
}
fun PropertiesBuilder.fontBigger(){
    fontSize = 2.rem
}


// BOX SIZE

fun PropertiesBuilder.boxNormalSmall(){
    width = 40.vw
    maxWidth = 40.rem
    minWidth = 240.px
    height = 20.vh
    maxHeight = 20.rem
    minHeight = 100.px
}

fun PropertiesBuilder.boxNormalNormal(){
    width = 50.vw
    maxWidth = 50.rem
    minWidth = 300.px
    height = "fit-content".unsafeCast<Height>()
    maxHeight = 50.rem
    minHeight = 200.px
}

fun PropertiesBuilder.boxNormalBig(){
    width = 60.vw
    maxWidth = 60.rem
    minWidth = 360.px
    height = 80.vh
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

fun PropertiesBuilder.colDefault(){
    padding = 0.px
    marginTop = 0.px
    marginBottom = 0.px
    contentCenteredVertically()
}

fun PropertiesBuilder.absolute0Pos(){
    position = Position.absolute
    right = 0.px
    top = 0.px
    left = 0.px
    bottom = 0.px
}

fun PropertiesBuilder.submitFileStyle(){
    contentCenteredHorizontally()
    height = 100.pct
    width = 100.pct
    cursor = Cursor.pointer
}

fun PropertiesBuilder.card(){
    width = 100.pct
    display = Display.flex
    flexDirection = FlexDirection.column
    alignItems = AlignItems.center
    borderRadius = 2.vh
    // This allows for the card to look like a card without the "Overflow: hidden" attribute
    maskImage = "url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAIAAACQd1PeAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAA5JREFUeNpiYGBgAAgwAAAEAAGbA+oJAAAAAElFTkSuQmCC);" as MaskImage /* this fixes the overflow:hidden in Chrome/Opera */

}

fun PropertiesBuilder.cardTopHalf(){
    divDefaultHorizontalCss()
    justifySpaceBetween()
    boxSizing = BoxSizing.borderBox
    width = 100.pct
    display = Display.flex
    alignItems = AlignItems.center
    borderColor = Color(Const.ColorCode.BACKGROUND_GREY_DARKER.code())
    backgroundColor = Color(Const.ColorCode.BACKGROUND_GREY_DARKER.code())
}

fun PropertiesBuilder.cardBottomHalf(){
    divDefaultHorizontalCss()
    justifySpaceBetween()
    boxSizing = BoxSizing.borderBox
    width = 100.pct
    display = Display.flex
    alignItems = AlignItems.center
    borderColor = Color(Const.ColorCode.BACKGROUND_GREY_DARKER.code())
    backgroundColor = Color(Const.ColorCode.BACKGROUND_GREY_DARK.code())
}

fun PropertiesBuilder.tabPanel(){
    overflow = Overflow.hidden
    boxSizing = BoxSizing.borderBox
    width = 100.pct
    height = 100.pct
}