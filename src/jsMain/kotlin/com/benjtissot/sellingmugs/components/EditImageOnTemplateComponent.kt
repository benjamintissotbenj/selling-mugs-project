package com.benjtissot.sellingmugs.components

import com.benjtissot.sellingmugs.contentCenteredHorizontally
import com.benjtissot.sellingmugs.entities.printify.ImageForUploadReceive
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
import ringui.Col
import ringui.Grid
import ringui.Row


external interface EditImageOnTemplateProps: Props {
    var uploadedImage: ImageForUploadReceive?
}

val EditImageOnTemplateComponent = FC<EditImageOnTemplateProps> { props ->
    var scale = 1
    val templateWidth = scale*40f
    val templateHeight = templateWidth/2f
    val gridUnit = templateWidth/10f
    val gridWidth = 12f*gridUnit
    val gridHeight = templateHeight + gridUnit
    val hwratio = (props.uploadedImage?.height?:1)/(props.uploadedImage?.width?:1)


    Grid {
        css {
            width = gridWidth.vw
            height = gridHeight.vw
            padding = 0.px
            margin = 16.px
        }
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
                xsOffset = 1
                xs = 10
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
                                top = -1f/4f*hwratio*templateWidth.vw
                                left = 0.px
                                bottom = 0.px
                                width = templateWidth.vw
                                // Height is auto
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
                xs = 1
                Slider {
                    sx {
                        "{\n" +
                                "    '& input[type=\"range\"]': {\n" +
                                "      WebkitAppearance: 'slider-vertical',\n" +
                                "    },\n" +
                                "  }"
                    }
                    size = Size.small
                    orientation = Orientation.vertical
                    defaultValue = "50"
                    valueLabelDisplay = "auto"
                }
            }
        }

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
                xs = 10
                xsOffset = 1
                Slider {
                    size = Size.small
                    defaultValue = "50"
                    valueLabelDisplay = "auto"
                }
            }
        }
    }



}