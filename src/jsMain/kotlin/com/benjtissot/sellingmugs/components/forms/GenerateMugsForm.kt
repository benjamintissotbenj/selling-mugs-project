package com.benjtissot.sellingmugs.components.forms

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.Const.ColorCode.BLUE
import csstype.*
import emotion.react.css
import mui.material.MenuItem
import mui.material.Select
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
import kotlin.math.roundToInt

external interface GenerateMugsFormProps : Props {
    var onSubmit: (String, Const.StableDiffusionImageType, Int) -> Unit
}

val GenerateMugsForm = FC<GenerateMugsFormProps> { props ->
    var subject by useState(Const.mugSubjectPrefill)
    var numberOfVariations by useState(2)
    var imageType by useState(Const.StableDiffusionImageType.REALISTIC.type)

    val submitHandler: FormEventHandler<HTMLFormElement> = {
        it.preventDefault()
        props.onSubmit(subject, Const.StableDiffusionImageType.valueOf(imageType), numberOfVariations)
    }

    div {
        css {
            fullCenterColumnOriented()
            width = 100.pct
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
                    div {
                        css {
                            padding = 2.vh
                        }
                        +"Subject"
                    }
                    input {
                        css {
                            formInput()
                            fontNormal()
                        }
                        type = InputType.text
                        onChange = {
                            subject = it.target.value
                        }
                        value = subject
                    }
                }

                // Number of Variations to create
                label {
                    css {
                        formLabel()
                        width = 100.pct
                    }
                    div {
                        css {
                            padding = 2.vh
                        }
                        +"Number of variations (<15)"
                    }
                    input {
                        css {
                            formInput()
                            fontNormal()
                        }
                        type = InputType.text
                        onChange = {
                            val numberOfVariationsTemp = try {
                                it.target.value.toInt()
                            } catch (e: NumberFormatException) {
                                0
                            }
                            if (numberOfVariationsTemp < 15){
                                numberOfVariations = numberOfVariationsTemp
                            }
                        }
                        value = numberOfVariations.toString()
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
                    div {
                        css {
                            padding = 2.vh
                        }
                        +"Art Type"
                    }
                    Select {
                        // Attributes
                        css {
                            width = 50.pct
                            minWidth = 110.px
                            height = 3.rem
                            maxHeight = 5.vh
                            minHeight = 40.px
                            color = NamedColor.black
                            boxSizing = BoxSizing.borderBox
                            fontNormal()
                        }
                        value = imageType
                        onChange = { event, _ ->
                            imageType = event.target.value
                        }


                        // Children, in the selector

                        MenuItem {
                            value = Const.StableDiffusionImageType.REALISTIC.type
                            +"Realistic"
                        }
                        MenuItem {
                            value = Const.StableDiffusionImageType.GEOMETRIC.type
                            +"Geometric"
                        }
                        MenuItem {
                            value = Const.StableDiffusionImageType.CARTOON_ILLUSTRATION.type
                            +"Cartoon Illustration"
                        }
                    }
                }
            }

            input {
                css {
                    formInput(10.vw, 200.px, backColor = Color(BLUE.code()), frontColor = NamedColor.white)
                    marginTop = 2.vh
                    cursor = Cursor.pointer
                }
                type = InputType.submit
                value = "Generate mugs"
            }

            onSubmit = submitHandler
        }
    }
}