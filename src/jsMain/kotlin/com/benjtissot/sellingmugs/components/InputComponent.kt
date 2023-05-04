package com.benjtissot.sellingmugs.components

import org.w3c.dom.HTMLFormElement
import react.*
import org.w3c.dom.HTMLInputElement
import react.dom.events.ChangeEventHandler
import react.dom.events.FormEventHandler
import react.dom.html.InputType
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.input

external interface InputProps : Props {
    var onSubmit: (String, String) -> Unit
}

val InputComponent = FC<InputProps> { props ->
    val (mugName, setMugName) = useState("")
    val (artURL, setArtURL) = useState("")

    val submitHandler: FormEventHandler<HTMLFormElement> = {
        it.preventDefault()
        props.onSubmit(mugName, artURL)
        setMugName("")
        setArtURL("")
    }

    val mugNameChangeHandler: ChangeEventHandler<HTMLInputElement> = {
        setMugName(it.target.value)
    }

    val artURLChangeHandler: ChangeEventHandler<HTMLInputElement> = {
        setArtURL(it.target.value)
    }

    form {

        input {
            type = InputType.text
            onChange = mugNameChangeHandler
            value = mugName
        }
        input {
            type = InputType.text
            onChange = artURLChangeHandler
            value = artURL
        }
        input {
            type = InputType.submit
            value = "Add Mug"
        }

        onSubmit = submitHandler
    }
}