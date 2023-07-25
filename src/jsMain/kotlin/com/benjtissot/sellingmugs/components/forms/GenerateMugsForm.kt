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
import react.useState

external interface GenerateMugsFormProps : Props {
    var onSubmit: (String, Const.StableDiffusionImageType, Int) -> Unit
    var isCustomMug : Boolean
    var width : Width
}

val GenerateMugsForm = FC<GenerateMugsFormProps> { props ->
    var subject by useState(Const.mugSubjectPrefill)
    var numberOfVariations by useState(if (props.isCustomMug) 1 else 2)
    var imageType by useState(Const.StableDiffusionImageType.REALISTIC.type)

    val submitHandler: FormEventHandler<HTMLFormElement> = {
        it.preventDefault()
        props.onSubmit(subject, Const.StableDiffusionImageType.valueOf(imageType), numberOfVariations)
    }

    div {
        css {
            fullCenterColumnOriented()
            width = props.width
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
                            padding = 1.vh
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

                // Number of Variations to create, available when not creating single mug
                if (!props.isCustomMug){
                    VariationInput {
                        this.numberOfVariations = numberOfVariations
                        onChange = { nbVarTemp ->
                            if (nbVarTemp < 15){
                                numberOfVariations = nbVarTemp
                            }
                        }
                    }
                }

                // Art Type
                ArtTypeInput {
                    artType = imageType
                    onChange = { value ->
                        imageType = value
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
                value = if (numberOfVariations == 1) "Generate mug" else "Generate mugs"
            }

            onSubmit = submitHandler
        }
    }
}

external interface GenerateCategoriesFormProps : Props {
    var onSubmit: (Int, Int, Const.StableDiffusionImageType?) -> Unit
}

val GenerateCategoriesForm = FC<GenerateCategoriesFormProps> { props ->
    var numberOfCategories by useState(2)
    var numberOfVariations by useState(2)
    var imageType by useState("Automatic")

    val submitHandler: FormEventHandler<HTMLFormElement> = {
        it.preventDefault()
        props.onSubmit(numberOfCategories, numberOfVariations, try {Const.StableDiffusionImageType.valueOf(imageType)} catch (e: Exception) {null})
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

                // Number of Categories to create
                label {
                    css {
                        formLabel()
                        width = 100.pct
                    }
                    div {
                        css {
                            padding = 1.vh
                        }
                        +"Number of Categories (<15)"
                    }
                    input {
                        css {
                            formInput()
                            fontNormal()
                        }
                        type = InputType.text
                        onChange = {
                            val numberOfCategoriesTemp = try {
                                it.target.value.toInt()
                            } catch (e: NumberFormatException) {
                                0
                            }
                            if (numberOfCategoriesTemp < 15){
                                numberOfCategories = numberOfCategoriesTemp
                            }
                        }
                        value = numberOfCategories.toString()
                    }
                }

                // Number of Variations to create
                VariationInput {
                    this.numberOfVariations = numberOfVariations
                    onChange = { nbVarTemp ->
                        if (nbVarTemp < 15){
                            numberOfVariations = nbVarTemp
                        }
                    }
                }

                // Art Type
                ArtTypeInput {
                    artType = imageType
                    onChange = { value ->
                        imageType = value
                    }
                    automaticPossible = true
                }
            }

            input {
                css {
                    formInput(80.pct, 200.px, backColor = Color(BLUE.code()), frontColor = NamedColor.white)
                    textAlign = TextAlign.center
                    marginTop = 2.vh
                    cursor = Cursor.pointer
                }
                type = InputType.submit
                value = "Generate $numberOfCategories categories with $numberOfVariations mugs each"
            }

            onSubmit = submitHandler
        }
    }
}

external interface VariationInputProps : Props {
    var onChange: (Int) -> Unit
    var numberOfVariations : Int
}

val VariationInput = FC<VariationInputProps> { props ->
    label {
        css {
            formLabel()
            width = 100.pct
        }
        div {
            css {
                padding = 1.vh
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
                props.onChange(
                    try {
                        it.target.value.toInt()
                    } catch (e: NumberFormatException) {
                        0
                    }
                )
            }
            value = props.numberOfVariations.toString()
        }
    }
}


external interface ArtTypeInputProps : Props {
    var onChange: (String) -> Unit
    var artType : String
    var automaticPossible : Boolean?
}
val ArtTypeInput = FC<ArtTypeInputProps> { props ->

    label {
        css {
            formLabel()
            width = 100.pct
            marginBottom = 1.vh
        }
        div {
            css {
                padding = 1.vh
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
            value = props.artType
            onChange = { event, _ ->
                props.onChange(event.target.value)
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
            if (props.automaticPossible == true) {
                MenuItem {
                    value = "Automatic"
                    +"Automatic"
                }
            }
        }
    }
}