package com.benjtissot.sellingmugs.components.forms

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.Const.ColorCode.BLUE
import com.benjtissot.sellingmugs.entities.RegisterInfo
import csstype.*
import emotion.react.css
import org.komputing.khash.sha256.extensions.sha256
import org.w3c.dom.HTMLFormElement
import react.ChildrenBuilder
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

    var errors: List<RegisterFormError> by useState(emptyList())

    val submitHandler: FormEventHandler<HTMLFormElement> = {
        it.preventDefault()
        val errorList = ArrayList<RegisterFormError>(emptyList())
        // Todo: put in better checks
        if (password.length < 6) errorList.add(PasswordLengthError())
        if (password != confirmPassword) errorList.add(PasswordMatchError())
        if (email.split("@").size < 2 || email.split("@")[1].split(".").size < 2) errorList.add(InvalidEmailError())

        if (errorList.isEmpty()){
            val registerInfo = RegisterInfo(firstName, lastName, email, password.sha256().toString())
            props.onSubmit(registerInfo)
            setEmail("")
            setFirstName("")
            setLastName("")
            setPassword("")
            setConfirmPassword("")
        } else {
            errors = errorList.toList()
        }
    }

    div {
        formComponentDivCss()
        form {
            formCss()

            div {
                formLabelGroupDivCss()

                // First Name
                label {
                    css { formLabel() }
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
                    css { formLabel() }
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

                // Helper method to display email error if there is any
                showEmailError(errors)

                // Email
                label {
                    css {
                        formLabel()
                        color = if (errors.contains(InvalidEmailError())) NamedColor.red else NamedColor.black
                    }
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

                // Helper method to display password errors
                showPasswordErrors(errors)

                // Password
                label {
                    css {
                        formLabel()
                        color = if (errors.contains(PasswordMatchError()) || errors.contains(PasswordLengthError())) NamedColor.red else NamedColor.black
                    }
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
                    css {
                        formLabel()
                        color = if (errors.contains(PasswordMatchError()) || errors.contains(PasswordLengthError())) NamedColor.red else NamedColor.black
                    }
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

/**
 * Shows divs with error messages concerning passwords if there are any
 */
fun ChildrenBuilder.showPasswordErrors(errors : List<RegisterFormError>){

    if (errors.contains(PasswordMatchError())){
        div {
            css {
                color = NamedColor.red
                paddingBlock = 1.vh
                fontSmall()
            }
            +PasswordMatchError().message
        }
    }

    if (errors.contains(PasswordLengthError())){
        div {
            css {
                color = NamedColor.red
                paddingBlock = 1.vh
                fontSmall()
            }
            +PasswordLengthError().message
        }
    }
}

/**
 * Shows a div with email Error if there is one
 */
fun ChildrenBuilder.showEmailError(errors : List<RegisterFormError>) {
    if (errors.contains(InvalidEmailError())) {
        div {
            css {
                color = NamedColor.red
                paddingBlock = 1.vh
                fontSmall()
            }
            +InvalidEmailError().message
        }
    }
}

/**
 * A superclass for different registering errors to be treated
 */
open class RegisterFormError(val message: String){
    override fun equals(other: Any?): Boolean {
        return (other is RegisterFormError) && other.message == this.message
    }

    override fun hashCode(): Int {
        return message.hashCode()
    }
}
class PasswordMatchError() : RegisterFormError("Passwords don't match"){}
class InvalidEmailError() : RegisterFormError("Email is not in a valid format"){}
class PasswordLengthError() : RegisterFormError("Password must be at least 6 characters long"){}