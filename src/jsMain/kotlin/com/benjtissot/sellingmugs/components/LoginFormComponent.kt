package com.benjtissot.sellingmugs.components

import com.benjtissot.sellingmugs.formInputCss
import csstype.*
import emotion.react.css
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.HTMLInputElement
import react.FC
import react.Props
import react.dom.events.ChangeEventHandler
import react.dom.events.FormEventHandler
import react.dom.html.InputType
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.useState

external interface LoginFormProps : Props {
    var onSubmit: (String, String) -> Unit
}

val LoginFormComponent = FC<LoginFormProps> { props ->
    val (email, setEmail) = useState("")
    val (password, setPassword) = useState("")

    val submitHandler: FormEventHandler<HTMLFormElement> = {
        it.preventDefault()
        props.onSubmit(email, password)
        setEmail("")
        setPassword("")
    }

    val emailChangeHandler: ChangeEventHandler<HTMLInputElement> = {
        setEmail(it.target.value)
    }

    val passwordChangeHandler: ChangeEventHandler<HTMLInputElement> = {
        setPassword(it.target.value)
    }

    form {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
        }
        label {
            css {
                marginBottom = 1.vh
            }
            +"Email"
            input {
                formInputCss()
                type = InputType.text
                onChange = emailChangeHandler
                value = email
            }
        }

        label {
            css {
                marginBottom = 1.vh
            }
            +"Password"
            input {
                formInputCss()
                type = InputType.password
                onChange = passwordChangeHandler
                value = password
            }
        }
        input {
            formInputCss(10.vw, backColor = Color("#007bff"), frontColor = NamedColor.white)
            type = InputType.submit
            value = "Login"
        }

        onSubmit = submitHandler
    }
}