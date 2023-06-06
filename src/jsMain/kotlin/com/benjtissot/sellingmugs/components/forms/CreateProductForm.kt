package com.benjtissot.sellingmugs.components.forms

import com.benjtissot.sellingmugs.*
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
import csstype.*
import emotion.react.css

external interface CreateProductFormProps : Props {
    var onSubmit: (String, String) -> Unit
    var uploadImageWarning: Boolean
}

val CreateProductForm = FC<CreateProductFormProps> { props ->
    var title by useState("")
    var description by useState("")

    val submitHandler: FormEventHandler<HTMLFormElement> = {
        it.preventDefault()
        props.onSubmit(title, description)
        title = ""
        description = ""
    }

    div {
        css {
            fullCenterColumnOriented()
        }
        form {
            css {
                contentCenteredHorizontally()
            }

            div {
                formLabelGroupDivCss()

                // First Name
                label {
                    formLabelCss()
                    +"Title"
                    input {
                        formInputCss()
                        type = InputType.text
                        onChange = {
                            title = it.target.value
                        }
                        value = title
                    }
                }

                // Last Name
                label {
                    formLabelCss()
                    +"Description of the product"
                    input {
                        formInputCss()
                        type = InputType.text
                        onChange = {
                            description = it.target.value
                        }
                        value = description
                    }
                }
            }

            if (props.uploadImageWarning){
                div {
                    css {
                        fontNormal()
                        color = Color(RED.code())
                        marginBottom = 1.vh
                    }
                    +"Please upload an image before creating a product"
                }
            }

            input {
                formInputCss(10.vw, 200.px, backColor = Color(BLUE.code()), frontColor = NamedColor.white)
                type = InputType.submit
                value = "Create Product"
            }

            onSubmit = submitHandler
        }
    }
}