package com.benjtissot.sellingmugs.components.forms

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.Const.ColorCode.BLUE
import csstype.*
import emotion.react.css
import org.w3c.dom.HTMLFormElement
import react.FC
import react.Props
import react.dom.events.FormEventHandler
import react.dom.html.InputType
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.form
import react.dom.html.ReactHTML.input
import react.dom.html.ReactHTML.label
import react.dom.html.ReactHTML.textarea
import react.useState

external interface CreateProductFormProps : Props {
    var onSubmit: (String, String) -> Unit
    var deleteFieldsOnSubmit: Boolean
}

val CreateProductForm = FC<CreateProductFormProps> { props ->
    var title by useState(Const.mugTitlePrefill)
    var description by useState(Const.mugDescriptionPrefill)

    val submitHandler: FormEventHandler<HTMLFormElement> = {
        it.preventDefault()
        props.onSubmit(title, description)
        if (props.deleteFieldsOnSubmit){
            title = ""
            description = ""
        }
    }

    div {
        css {
            fullCenterColumnOriented()
        }
        form {
            css {
                contentCenteredHorizontally()
                width = 100.pct
            }

            div {
                formLabelGroupDivCss()

                // Product title
                label {
                    css {
                        formLabel()
                        width = 100.pct
                    }
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

                // Product Description
                label {
                    css {
                        formLabel()
                        height = 10.vw
                        width = 100.pct
                        marginBottom = 1.vw
                    }
                    +"Description of the product"
                    textarea {
                        css {
                            formInput()
                            maskImage = Const.maskUrl as MaskImage /* this fixes the overflow:hidden in Chrome/Opera */
                            height = 100.pct
                        }
                        onChange = {
                            description = it.target.value
                        }
                        value = description
                    }
                }
            }

            input {

                css {
                    formInput(10.vw, 200.px, backColor = Color(BLUE.code()), frontColor = NamedColor.white)
                    marginTop = 2.vh
                }
                type = InputType.submit
                value = "Create Product"
            }

            onSubmit = submitHandler
        }
    }
}