package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.components.FooterComponent
import com.benjtissot.sellingmugs.components.InputComponent
import com.benjtissot.sellingmugs.components.MugListComponent
import com.benjtissot.sellingmugs.components.NavigationBarComponent
import com.benjtissot.sellingmugs.entities.Artwork
import com.benjtissot.sellingmugs.entities.Mug
import com.benjtissot.sellingmugs.entities.Session
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import react.FC
import react.Props
import react.useEffectOnce
import react.useState

external interface HomepageProps : Props {
}

private val scope = MainScope()

val Homepage = FC<HomepageProps> { props ->
    var mugList by useState(emptyList<Mug>())
    var session: Session? by useState(null)

    // At first initialisation, get the list
    // Alternative is useState when we want to persist something across re-renders
    useEffectOnce {
        scope.launch {
            mugList = getMugList()
        }
        scope.launch {
            session = getSession()
        }
    }
    session?.also{
        NavigationBarComponent {
            currentSession = session!!
            updateSession = {
                scope.launch {
                    session = getSession()
                }
            }
        }

        MugListComponent {
            list = mugList
            title = "Best for you"
            onItemClick = {
                scope.launch {
                    deleteMugListItem(it) // deletes from server
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
    } ?:

    FooterComponent {}
}