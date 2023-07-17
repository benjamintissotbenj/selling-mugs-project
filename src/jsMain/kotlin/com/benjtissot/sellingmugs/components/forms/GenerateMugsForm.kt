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

external interface GenerateMugsFormProps : Props {
    var onSubmit: (String, Const.StableDiffusionImageType) -> Unit
}

val GenerateMugsForm = FC<GenerateMugsFormProps> { props ->
    var subject by useState(Const.mugSubjectPrefill)
    var imageType by useState(Const.StableDiffusionImageType.REALISTIC.type)

    val submitHandler: FormEventHandler<HTMLFormElement> = {
        it.preventDefault()
        props.onSubmit(subject, Const.StableDiffusionImageType.valueOf(imageType))
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

                // Mug Subject
                label {
                    css {
                        formLabel()
                        width = 100.pct
                    }
                    +"Subject"
                    input {
                        formInputCss()
                        type = InputType.text
                        onChange = {
                            subject = it.target.value
                        }
                        value = subject
                    }
                }

                // Art Type
                label {
                    css {
                        formLabel()
                        height = 10.vw
                        width = 100.pct
                        marginBottom = 1.vw
                    }
                    +"Art Type"
                    textarea {
                        css {
                            formInput()
                            maskImage = Const.maskUrl as MaskImage /* this fixes the overflow:hidden in Chrome/Opera */
                            height = 100.pct
                        }
                        onChange = {
                            imageType = it.target.value
                        }
                        value = imageType
                    }
                }
            }

            input {
                formInputCss(10.vw, 200.px, backColor = Color(BLUE.code()), frontColor = NamedColor.white)
                type = InputType.submit
                value = "Generate mugs"
            }

            onSubmit = submitHandler
        }
    }
}