package com.benjtissot.sellingmugs.components.forms

import com.benjtissot.sellingmugs.*
import csstype.*
import emotion.react.css
import mui.icons.material.Close
import mui.icons.material.Search
import org.w3c.dom.HTMLFormElement
import react.FC
import react.Props
import react.dom.events.FormEventHandler
import react.dom.html.InputType
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.useState

external interface SearchBarProps : Props {
    var searchString: String
    var onSubmit: (String) -> Unit
}

val SearchBarComponent = FC<SearchBarProps> { props ->
    var searchString by useState(props.searchString)

    val submitHandler: FormEventHandler<HTMLFormElement> = {
        it.preventDefault()
        props.onSubmit(searchString)
    }

    form {
        css {
            padding = 1.vh
            width = 100.pct
            display = Display.flex
            flexDirection = FlexDirection.row
            alignItems = AlignItems.center
        }

        // Search
        label {
            css {
                formLabel()
                fontNormal()
                marginBottom = 0.px
                width = 100.pct
                display = Display.flex
                flexDirection = FlexDirection.row
                alignItems = AlignItems.center
            }
            +"Search"
            input {
                css {
                    formInput(100.pct, 180.px, null, null)
                    marginBottom = 0.px
                    marginLeft = 10.pct
                    marginRight = 3.pct
                    display = Display.flex
                    flexDirection = FlexDirection.row
                    alignItems = AlignItems.center
                }
                placeholder = "Mug name, vibe, ..."
                type = InputType.text
                onChange = {
                    searchString = it.target.value
                }
                value = searchString
            }
            Search {
                css {
                    cursor = Cursor.pointer
                    marginRight = 1.vh
                }
                onClick = {
                    props.onSubmit(searchString)
                }
            }
            Close {
                css {
                    cursor = Cursor.pointer
                }
                onClick = {
                    searchString = ""
                    props.onSubmit("")
                }
            }
        }


        onSubmit = submitHandler
    }


}