package com.benjtissot.sellingmugs.components.createProduct

import com.benjtissot.sellingmugs.*
import csstype.*
import emotion.react.css
import io.ktor.util.logging.*
import mui.icons.material.Upload
import mui.material.Icon
import org.w3c.dom.DragEvent
import org.w3c.dom.Element
import org.w3c.dom.asList
import org.w3c.dom.events.Event
import org.w3c.files.File
import react.*
import react.dom.html.InputType
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label

private val LOG = KtorSimpleLogger("DragDropComponent.kt")

external interface ImageDropProps : Props {
    var onImageDrop: (List<File>) -> Unit
    var width: Width?
    var height: Height?
}


val ImageDrop = FC<ImageDropProps> { props ->
    val dropZoneRef = useRef<Element>(null)
    var isDragging by useState { false }

    useEffect {
        val dropZone = dropZoneRef.current

        val handleDragEnter: (Event) -> Unit = { event ->
            event.preventDefault()
            isDragging = true
        }

        val handleDragOver: (Event) -> Unit = { event ->
            event.preventDefault()
            if (!isDragging){
                isDragging = true
            }
        }

        val handleDragLeave: (Event) -> Unit = { event ->
            event.preventDefault()
            isDragging = false
        }

        val handleDrop: (Event) -> Unit = { event ->
            event.preventDefault()
            if (isDragging){
                val dragEvent = event as DragEvent
                LOG.debug("The received event's data transfer is ${dragEvent.dataTransfer}")

                val fileList = dragEvent.dataTransfer?.files?.asList() as List<File>
                LOG.debug("The received event's files is $fileList")
                props.onImageDrop(fileList)
                isDragging = false
            }

        }

        dropZone?.addEventListener("dragenter", handleDragEnter)
        dropZone?.addEventListener("dragover", handleDragOver)
        dropZone?.addEventListener("dragleave", handleDragLeave)
        dropZone?.addEventListener("drop", handleDrop)

    }

    div {

        css {
            boxNormalSmall()
            width = props.width?:40.vw
            height = props.height?:20.vh
            if (isDragging){
                boxBlueShade()
            } else {
                boxShade()
            }
            contentCenteredVertically()
            justifyContent = JustifyContent.center
            marginTop = 2.vw
        }

        ref = dropZoneRef

        form {
            css {
                fontNormal()
                submitFileStyle()
            }
            label {
                css {
                    submitFileStyle()
                    justifyContent = JustifyContent.center
                }
                div {
                    +"Drag and Drop image"
                }
                div {
                    css {
                        fontWeight = FontWeight.bold
                    }
                    +"OR"
                }
                div {
                    +"Click to choose a file "
                }

                Icon {
                    Upload()
                }

                input {
                    css {
                        fontNormal()
                        display = "none".unsafeCast<Display>()
                    }
                    type = InputType.file
                    onChange = {
                        props.onImageDrop((it.target.files?.asList() as List<File>))
                    }
                }
            }
        }
    }
}
