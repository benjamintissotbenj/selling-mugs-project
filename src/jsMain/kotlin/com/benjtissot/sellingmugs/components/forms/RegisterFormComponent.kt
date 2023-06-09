package com.benjtissot.sellingmugs.components.forms

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.Const.ColorCode.BLUE
import com.benjtissot.sellingmugs.entities.RegisterInfo
import csstype.Color
import csstype.NamedColor
import csstype.px
import csstype.vw
import org.komputing.khash.sha256.extensions.sha256
import org.w3c.dom.HTMLFormElement
import react.FC
import react.Props
import react.dom.events.FormEventHandler
import react.dom.html.AutoComplete
import react.dom.html.InputType
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.useState

external interface RegisterFormProps : Props {
    var onSubmit: (RegisterInfo) -> Unit
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
            val registerInfo = RegisterInfo(firstName, lastName, email, password.sha256().toString())
            props.onSubmit(registerInfo)
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
                        autoComplete = AutoComplete.givenName
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
                        autoComplete = AutoComplete.familyName
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
                        autoComplete = AutoComplete.email
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
                        autoComplete = AutoComplete.newPassword
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
                        autoComplete = AutoComplete.newPassword
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