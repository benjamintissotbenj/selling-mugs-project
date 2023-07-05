package com.benjtissot.sellingmugs.components.createProduct

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.marksPosSlider
import com.benjtissot.sellingmugs.components.marksRotateSlider
import com.benjtissot.sellingmugs.components.marksScaleSlider
import com.benjtissot.sellingmugs.entities.printify.Image
import com.benjtissot.sellingmugs.entities.printify.ImageForUploadReceive
import com.benjtissot.sellingmugs.entities.printify.ReceiveProduct
import csstype.*
import emotion.react.css
import kotlinx.coroutines.launch
import mui.icons.material.Refresh
import mui.material.IconButton
import mui.material.Orientation
import mui.material.Size
import mui.material.Slider
import mui.system.sx
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img
import react.useState
import ringui.Col
import ringui.Grid
import ringui.Row
import kotlin.Float
import kotlin.math.round

external interface EditImageOnTemplateProps: Props {
    var uploadedImage: ImageForUploadReceive?
    var receiveProduct: ReceiveProduct?
    var updateProduct: (ReceiveProduct)->Unit
}


val EditImageOnTemplateComponent = FC<EditImageOnTemplateProps> { props ->

    val uploadedImage = props.uploadedImage
    val receiveProduct = props.receiveProduct

    var scale by useState(1f) // ratio image_width / template_width
    var rotate by useState(0) // [-180 ; 180]]
    var horizontalPositionPercentage by useState(50) // from 0 to 100
    var verticalPositionPercentage by useState(50) // from 0 to 100

    val templateWidth = 40f
    val templateHeight = templateWidth/2f
    val gridUnit = templateWidth/8f
    val gridWidth = 12f*gridUnit
    val gridHeight = templateHeight + gridUnit
    val hwratio = (uploadedImage?.height?:1).toFloat()/(uploadedImage?.width?:1).toFloat()

    val imageWidth = scale*templateWidth
    val imageHeight = hwratio*imageWidth

    val x = (horizontalPositionPercentage*2f-50f)/100f // from -0.5 to +1.5, as it is the position of the center
    val y = (verticalPositionPercentage*2f-50f)/100f // from -0.5 to +1.5, as it is the position of the center

    Grid {
        css {
            width = gridWidth.vw
            height = "fit-content".unsafeCast<Height>()
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
                    colDefault()
                }
                xsOffset = 2
                xs = 8

                // Template and overlayed image
                ImageOnTemplateComponent {
                    this.uploadedImage = uploadedImage
                    this.templateHeight = templateHeight
                    this.templateWidth = templateWidth
                    this.imageHeight = imageHeight
                    this.imageWidth = imageWidth
                    this.scale = scale
                    this.rotate = rotate
                    this.x = x
                    this.y = y
                }
            }

            Col {
                css {
                    colDefault()
                }
                xs = 2
                Slider {
                    sx {
                        @Suppress("UNUSED_EXPRESSION")
                        "{\n'& input[type=\"range\"]': {\nWebkitAppearance: 'slider-vertical',\n},\n}"
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
                    colDefault()
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
                    colDefault()
                    fontNormal()
                }
                xs = 2
                xsOffset = 1
                +"Scale :"
            }
            Col {
                css {
                    colDefault()
                }
                xs = 6
                xsOffset = 1

                Slider {
                    marks = marksScaleSlider
                    size = Size.small
                    defaultValue = 100.asDynamic()
                    min = 10
                    max = 300
                    valueLabelDisplay = "auto"
                    value = (scale*100f).toInt()
                    onChange = {evt, value, thumb ->
                        scale = round(value as Float)/100f
                    }
                }
            }
        }

        // Rotation slider
        Row {
            css {
                padding = 0.px
                margin = 0.px
            }
            Col {
                css {
                    colDefault()
                    fontNormal()
                }
                xs = 2
                xsOffset = 1
                +"Rotate :"
            }
            Col {
                css {
                    colDefault()
                }
                xs = 6
                xsOffset = 1

                Slider {
                    marks = marksRotateSlider
                    size = Size.small
                    defaultValue = 0.asDynamic()
                    min = -180
                    max = 180
                    valueLabelDisplay = "auto"
                    value = rotate
                    onChange = {evt, value, thumb ->
                        rotate = value
                    }
                }
            }
        }


        // Refresh Preview button
        Row {
            Col {
                css {
                    colDefault()
                    fontNormal()
                    contentCenteredHorizontally()
                }
                xs = 6
                xsOffset = 3
                div {
                    IconButton {
                        Refresh()
                        div {
                            css {
                                fontNormal()
                                margin = 2.vw
                            }
                            +"Refresh Preview"
                        }
                        onClick = {
                            // Put Update in printify
                            if (uploadedImage != null && receiveProduct != null) {
                                val transformedImage = Image(
                                    uploadedImage.id,
                                    uploadedImage.file_name,
                                    uploadedImage.mime_type,
                                    uploadedImage.height,
                                    uploadedImage.width,
                                    x,
                                    y,
                                    scale,
                                    rotate
                                )
                                scope.launch {
                                    props.updateProduct(putProduct(receiveProduct.id, receiveProduct.changeImage(transformedImage)))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}


external interface ImageOnTemplateProps: Props {
    var uploadedImage: ImageForUploadReceive?
    var templateHeight: Float
    var templateWidth: Float
    var imageHeight: Float
    var imageWidth: Float
    var scale: Float
    var rotate: Int
    var x: Float
    var y: Float
}

val ImageOnTemplateComponent = FC<ImageOnTemplateProps> { props ->
    div {
        css {
            overflow = Overflow.hidden
            position = Position.relative
            width = props.templateWidth.vw
            height = props.templateHeight.vw
            margin = 0.px
            zIndex = "5".unsafeCast<ZIndex>()
        }

        // Template in the background
        img {
            css {
                absolute0Pos()
                width = props.templateWidth.vw
                height = (props.templateWidth/2).vw
                zIndex = "3".unsafeCast<ZIndex>()
            }
            src = "static/print_template_mug.png"
        }

        // Image in the foreground
        if (props.uploadedImage != null){
            img {
                css {
                    absolute0Pos()
                    top = ((2*props.y*props.templateHeight - props.imageHeight)/2f).vw
                    left = ((2*props.x*props.templateWidth - props.imageWidth)/2f).vw
                    width = props.imageWidth.vw
                    height = props.imageHeight.vw
                    objectFit = ObjectFit.contain
                    opacity = "0.5".unsafeCast<Opacity>()
                    transform = rotate(props.rotate.deg)
                    zIndex = "4".unsafeCast<ZIndex>()
                }
                src = props.uploadedImage?.preview_url
            }
        }
    }
}