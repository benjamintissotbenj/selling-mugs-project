package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.highLevel.FooterComponent
import com.benjtissot.sellingmugs.components.forms.CreateMugComponent
import com.benjtissot.sellingmugs.components.lists.MugListComponent
import com.benjtissot.sellingmugs.components.highLevel.NavigationBarComponent
import com.benjtissot.sellingmugs.entities.Artwork
import com.benjtissot.sellingmugs.entities.Mug
import emotion.react.css
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import react.FC
import react.dom.html.ReactHTML.div
import react.router.useNavigate
import react.useEffectOnce
import react.useState

external interface HomepageProps : SessionPageProps {
}

var checkRedirect: String? = null

val Homepage = FC<HomepageProps> { props ->
    val navigateFun = useNavigate()
    var mugList by useState(emptyList<Mug>())

    // At first initialisation, get the list
    // Alternative is useState when we want to persist something across re-renders
    useEffectOnce {
        scope.launch {
            checkRedirect = checkRedirect()
            if (checkRedirect!= null && ALL_FRONT_END_PATHS.contains(checkRedirect)) {
                navigateFun.invoke(checkRedirect?:"")
                checkRedirect = ""
            } else {
                mugList = getMugList()
            }
        }
    }

    checkRedirect?.let {

        NavigationBarComponent {
            session = props.session
            updateSession = props.updateSession
            navigate = navigateFun
        }

        div {
            css {
                mainPageDiv()
            }
            MugListComponent {
                list = mugList
                title = "Best for you"
                onItemClick = { mug ->
                    scope.launch {
                        // Adding the mug to the cart
                        addMugToCart(mug)
                        mugList = getMugList() // updates client
                    }
                }
            }

            // Creating a field to input a new element
            CreateMugComponent {
                onSubmit = { mugName, artURL ->
                    val artwork = Artwork("", "", artURL, true)
                    val cartItem = Mug("", mugName, "DefaultDescription", 8.99f, artwork)

                    // Using a channel to have a sequential execution
                    val channel = Channel<Job>(capacity = Channel.UNLIMITED).apply {
                        scope.launch {
                            consumeEach { it.join() }
                        }
                    }
                    channel.trySend(scope.launch {
                        addArtwork(artwork)
                    })
                    channel.trySend(scope.launch {
                        addMugListItem(cartItem)
                        mugList = getMugList() // updates the state (using "useState") so re-renders page
                    })


                }
            }
        }

        FooterComponent {}
    }
}