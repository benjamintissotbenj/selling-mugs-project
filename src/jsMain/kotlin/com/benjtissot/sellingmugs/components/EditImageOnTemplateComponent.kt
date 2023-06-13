package com.benjtissot.sellingmugs.components

import com.benjtissot.sellingmugs.entities.printify.ImageForUploadReceive
import com.benjtissot.sellingmugs.uploadImage
import csstype.*
import emotion.react.css
import react.FC
import react.Props
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.img


external interface EditImageOnTemplateProps: Props {
    var uploadedImage: ImageForUploadReceive?
}

val EditImageOnTemplateComponent = FC<EditImageOnTemplateProps> { props ->
    var scale = 1
    val templateWidth = scale*40
    val hwratio = (props.uploadedImage?.height?:1)/(props.uploadedImage?.width?:1)

    div {

        css {
            overflow = Overflow.hidden
            position = Position.relative
            width = templateWidth.vw
            height = (templateWidth/2).vw
            margin = 4.vw
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