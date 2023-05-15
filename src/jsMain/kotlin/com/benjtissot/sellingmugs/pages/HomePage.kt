package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.FooterComponent
import com.benjtissot.sellingmugs.components.InputComponent
import com.benjtissot.sellingmugs.components.MugListComponent
import com.benjtissot.sellingmugs.components.NavigationBarComponent
import com.benjtissot.sellingmugs.entities.Artwork
import com.benjtissot.sellingmugs.entities.Mug
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import react.FC
import react.router.useNavigate
import react.useEffectOnce
import react.useState

external interface HomepageProps : SessionPageProps {
}

val Homepage = FC<HomepageProps> { props ->
    val navigateFun = useNavigate()
    var mugList by useState(emptyList<Mug>())

    // At first initialisation, get the list
    // Alternative is useState when we want to persist something across re-renders
    useEffectOnce {
        scope.launch {
            mugList = getMugList()
        }
    }
    NavigationBarComponent {
        session = props.session
        updateSession = props.updateSession
        navigate = navigateFun
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
    InputComponent {
        onSubmit = { mugName, artURL ->
            val artwork = Artwork("", artURL)
            val cartItem = Mug("", mugName, 8.99f, artwork)

            // Using a channel to have a sequential execution
            val channel = Channel<Job>(capacity = Channel.UNLIMITED).apply {
                scope.launch {
                    consumeEach { it.join() }
                }
            }
            channel.trySend(scope.launch{
                addArtwork(artwork)
            })
            channel.trySend(scope.launch{
                addMugListItem(cartItem)
                mugList = getMugList() // updates the state (using "useState") so re-renders page
            })


        }
    }

    FooterComponent {}
}