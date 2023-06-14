package com.benjtissot.sellingmugs.components

class SliderMark(
    var value: Int,
    var label: String
) {
}

val marksPosSlider = arrayOf(
    SliderMark(0, "0"),
    SliderMark(25, "25"),
    SliderMark(50, "50"),
    SliderMark(75, "75"),
    SliderMark(100, "100"),
)
val marksScaleSlider = arrayOf(
    SliderMark(10, "10"),
    SliderMark(100, "100"),
    SliderMark(200, "200"),
    SliderMark(300, "300"),
)
val marksRotateSlider = arrayOf(
    SliderMark(-180, "-180°"),
    SliderMark(-90, "-90°"),
    SliderMark(0, "0°"),
    SliderMark(90, "90°"),
    SliderMark(180, "180°"),
)