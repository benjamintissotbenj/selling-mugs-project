package com.benjtissot.sellingmugs.components

import com.benjtissot.sellingmugs.contentCenteredHorizontally
import com.benjtissot.sellingmugs.entities.printify.ImageForUploadReceive
import com.benjtissot.sellingmugs.fontNormal
import csstype.*
import emotion.react.css
import mui.material.GridDirection
import mui.material.Orientation
import mui.material.Size
import mui.material.Slider
import mui.system.Union
import mui.system.responsive
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import react.useState
import ringui.Col
import ringui.Grid
import ringui.Row

val marksPosSlider = arrayOf(
    SliderMark(0, "0"),
    SliderMark(25, "25"),
    SliderMark(50, "50"),
    SliderMark(75, "75"),
    SliderMark(100, "100"),
)

external interface EditImageOnTemplateProps: Props {
    var uploadedImage: ImageForUploadReceive?
}


val EditImageOnTemplateComponent = FC<EditImageOnTemplateProps> { props ->

    var scale by useState(1f) // ratio image_width / template_width
    var horizontalPositionPercentage by useState(50) // from 0 to 100
    var verticalPositionPercentage by useState(50) // from 0 to 100

    val templateWidth = 40f
    val templateHeight = templateWidth/2f
    val gridUnit = templateWidth/8f
    val gridWidth = 12f*gridUnit
    val gridHeight = templateHeight + gridUnit
    val hwratio = (props.uploadedImage?.height?:1).toFloat()/(props.uploadedImage?.width?:1).toFloat()

    val imageWidth = scale*templateWidth
    val imageHeight = hwratio*imageWidth

    val x = (horizontalPositionPercentage*2f-50f)/100f // from -0.5 to +1.5, as it is the position of the center
    val y = (verticalPositionPercentage*2f-50f)/100f // from -0.5 to +1.5, as it is the position of the center

    Grid {
        css {
            width = gridWidth.vw
            height = gridHeight.vw
            padding = 0.px
            margin = 16.px
        }

        // Template images + y slider
        Row {
            css {
                padding = 0.px
                margin = 0.px
            }
            Col {
                css {
                    padding = 0.px
                    marginTop = 0.px
                    marginBottom = 0.px
                    contentCenteredHorizontally()
                }
                xsOffset = 2
                xs = 8
                // Template and overlayed image
                div {
                    css {
                        overflow = Overflow.hidden
                        position = Position.relative
                        width = templateWidth.vw
                        height = templateHeight.vw
                        margin = 0.px
                    }

                    // Template in the background
                    img {
                        css {
                            position = Position.absolute
                            right = 0.px
                            top = 0.px
                            left = 0.px
                            bottom = 0.px
                            width = templateWidth.vw
                            height = (templateWidth/2).vw
                        }
                        src = "/print_template.png"
                    }

                    // Image in the foreground
                    if (props.uploadedImage != null){
                        img {
                            css {
                                position = Position.absolute
                                right = 0.px
                                top = ((2*y*templateHeight - imageHeight)/2f).vw
                                left = ((2*x*templateWidth - imageWidth)/2f).vw
                                bottom = 0.px
                                width = imageWidth.vw
                                height = imageHeight.vw
                                objectFit = ObjectFit.contain
                                opacity = "0.5".unsafeCast<Opacity>()
                            }
                            src = props.uploadedImage?.preview_url
                        }
                    }
                }
            }

            Col {
                css {
                    padding = 0.px
                    marginTop = 0.px
                    marginBottom = 0.px
                    display = Display.flex
                    flexDirection = FlexDirection.column
                    alignItems = AlignItems.center
                }
                xs = 2
                Slider {
                    sx {
                        "{\n" +
                                "    '& input[type=\"range\"]': {\n" +
                                "      WebkitAppearance: 'slider-vertical',\n" +
                                "    },\n" +
                                "  }"
                    }
                    marks = marksPosSlider
                    size = Size.small
                    orientation = Orientation.vertical
                    defaultValue = 50.asDynamic()
                    min = 0
                    max = 100
                    valueLabelDisplay = "auto"
                    value = 100 - verticalPositionPercentage
                    onChange = {evt, value, thumb ->
                        verticalPositionPercentage = 100 - value as Int
                    }
                }
            }
        }

        // x slider
        Row {
            css {
                padding = 0.px
                margin = 0.px
            }
            Col {
                css {
                    padding = 0.px
                    marginTop = 0.px
                    marginBottom = 0.px
                    display = Display.flex
                    flexDirection = FlexDirection.row
                    alignItems = AlignItems.center
                }
                xs = 8
                xsOffset = 2
                Slider {
                    marks = marksPosSlider
                    size = Size.small
                    defaultValue = 50.asDynamic()
                    min = 0
                    max = 100
                    valueLabelDisplay = "auto"
                    value = horizontalPositionPercentage
                    onChange = {evt, value, thumb ->
                        horizontalPositionPercentage = value as Int
                    }
                }
            }
        }

        // Scale slider
        Row {
            css {
                padding = 0.px
                margin = 0.px
            }
            Col {
                css {
                    padding = 0.px
                    marginTop = 0.px
                    marginBottom = 0.px
                    display = Display.flex
                    flexDirection = FlexDirection.row
                    alignItems = AlignItems.center
                    fontNormal()
                }
                xs = 2
                xsOffset = 1
                +"Scale :"
            }
            Col {
                css {
                    padding = 0.px
                    marginTop = 0.px
                    marginBottom = 0.px
                    display = Display.flex
                    flexDirection = FlexDirection.row
                    alignItems = AlignItems.center
                }
                xs = 6
                xsOffset = 1

                Slider {
                    size = Size.small
                    defaultValue = 100.asDynamic()
                    min = 10
                    max = 300
                    valueLabelDisplay = "auto"
                    value = scale*100f
                    onChange = {evt, value, thumb ->
                        scale = value/100f
                    }
                }
            }
        }
    }



}