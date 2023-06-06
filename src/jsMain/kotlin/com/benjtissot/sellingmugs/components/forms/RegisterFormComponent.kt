package com.benjtissot.sellingmugs.components.forms

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.entities.User
import csstype.Color
import csstype.NamedColor
import csstype.px
import csstype.vw
import org.komputing.khash.sha256.extensions.sha256
import org.w3c.dom.HTMLFormElement
import react.FC
import react.Props
import react.dom.events.FormEventHandler
import react.dom.html.InputType
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.useState
import com.benjtissot.sellingmugs.Const.ColorCode.*

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

    div {
        formComponentDivCss()
        form {
            formCss()

            div {
                formLabelGroupDivCss()

                // First Name
                label {
                    formLabelCss()
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
                    formLabelCss()
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
                    formLabelCss()
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
                    formLabelCss()
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
                    formLabelCss()
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
            }

            input {
                formInputCss(10.vw, 80.px, backColor = Color(BLUE.code()), frontColor = NamedColor.white)
                type = InputType.submit
                value = "Register"
            }

            onSubmit = submitHandler
        }
    }
}