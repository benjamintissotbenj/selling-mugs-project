import io.ktor.util.logging.*
import org.w3c.dom.DragEvent
import org.w3c.dom.Element
import org.w3c.dom.asList
import org.w3c.dom.events.Event
import org.w3c.files.File
import react.*
import react.dom.html.ReactHTML.div

private val LOG = KtorSimpleLogger("DragDropComponent.kt")

external interface ImageDropProps : Props {
    var onImageDrop: (List<File>) -> Unit
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
        }

        val handleDragLeave: (Event) -> Unit = { event ->
            event.preventDefault()
            isDragging = false
        }

        val handleDrop: (Event) -> Unit = { event ->
            val dragEvent = event as DragEvent
            event.preventDefault()
            isDragging = false
            LOG.debug("The received event's data transfer is ${dragEvent.dataTransfer}")

            val fileList = dragEvent.dataTransfer?.files?.asList() as List<File> ?: emptyList()
            LOG.debug("The received event's files is ${fileList}")
            props.onImageDrop(fileList)
        }

        dropZone?.addEventListener("dragenter", handleDragEnter)
        dropZone?.addEventListener("dragover", handleDragOver)
        dropZone?.addEventListener("dragleave", handleDragLeave)
        dropZone?.addEventListener("drop", handleDrop)

    }

    div {

        ref = dropZoneRef
        // TODO change style when dragging
        +"Drag and drop images here"
    }
}
