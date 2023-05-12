package com.benjtissot.sellingmugs.components

import com.benjtissot.sellingmugs.Const
import com.benjtissot.sellingmugs.entities.User
import com.benjtissot.sellingmugs.formInputCss
import csstype.*
import emotion.react.css
import org.komputing.khash.sha256.extensions.sha256
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

external interface RegisterFormProps : Props {
    var onSubmit: (User) -> Unit
}

val RegisterFormComponent = FC<RegisterFormProps> { props ->
    val (firstName, setFirstName) = useState("")
    val (lastName, setLastName) = useState("")
    val (email, setEmail) = useState("")
    val (password, setPassword) = useState("")
    val (confirmPassword, setConfirmPassword) = useState("")

    val submitHandler: FormEventHandler<HTMLFormElement> = {
        it.preventDefault()
        // Todo: put in better checks
        if (password.isNotBlank() && password == confirmPassword){
            val user = User("", firstName, lastName, email, password.sha256().toString(), Const.UserType.CLIENT, "")
            props.onSubmit(user)
            setEmail("")
            setFirstName("")
            setLastName("")
        }
        setPassword("")
        setConfirmPassword("")
    }

    form {
        css {
            display = Display.flex
            flexDirection = FlexDirection.column
            alignItems = AlignItems.center
        }

        // First Name
        label {
            css {
                marginBottom = 1.vh
            }
            +"First Name"
            input {
                formInputCss()
                type = InputType.text
                onChange = {
                    setFirstName(it.target.value)
                }
                value = firstName
            }
        }

        // Last Name
        label {
            css {
                marginBottom = 1.vh
            }
            +"Last Name"
            input {
                formInputCss()
                type = InputType.text
                onChange = {
                    setLastName(it.target.value)
                }
                value = lastName
            }
        }

        // Email
        label {
            css {
                marginBottom = 1.vh
            }
            +"Email"
            input {
                formInputCss()
                type = InputType.text
                onChange = {
                    setEmail(it.target.value)
                }
                value = email
            }
        }

        // Password
        label {
            css {
                marginBottom = 1.vh
            }
            +"Password"
            input {
                formInputCss()
                type = InputType.password
                onChange = {
                    setPassword(it.target.value)
                }
                value = password
            }
        }

        // Confirm Password
        label {
            css {
                marginBottom = 1.vh
            }
            +"Confirm Password"
            input {
                formInputCss()
                type = InputType.password
                onChange = {
                    setConfirmPassword(it.target.value)
                }
                value = confirmPassword
            }
        }


        input {
            formInputCss(10.vw, backColor = Color("#007bff"), frontColor = NamedColor.white)
            type = InputType.submit
            value = "Register"
        }

        onSubmit = submitHandler
    }
}