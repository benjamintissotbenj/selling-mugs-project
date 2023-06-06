package com.benjtissot.sellingmugs.components.forms

import com.benjtissot.sellingmugs.*
import csstype.Color
import csstype.NamedColor
import csstype.px
import csstype.vw
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.HTMLInputElement
import react.FC
import react.Props
import react.dom.events.ChangeEventHandler
import react.dom.events.FormEventHandler
import react.dom.html.InputType
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.useState
import com.benjtissot.sellingmugs.Const.ColorCode.*

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

    div {
        formComponentDivCss()

        form {
            formCss()
            div {
                formLabelGroupDivCss()

                // Email
                label {
                    formLabelCss()
                    +"Email"
                    input {
                        formInputCss()
                        type = InputType.text
                        onChange = emailChangeHandler
                        value = email
                    }
                }

                // Password
                label {
                    formLabelCss()
                    +"Password"
                    input {
                        formInputCss()
                        type = InputType.password
                        onChange = passwordChangeHandler
                        value = password
                    }
                }
            }
            input {
                formInputCss(10.vw, 80.px, backColor = Color(BLUE.code()), frontColor = NamedColor.white)
                type = InputType.submit
                value = "Login"
            }

            onSubmit = submitHandler
        }
    }

}