package com.benjtissot.sellingmugs

import com.benjtissot.sellingmugs.components.InputComponent
import com.benjtissot.sellingmugs.components.MugListComponent
import com.benjtissot.sellingmugs.components.NavigationBarComponent
import com.benjtissot.sellingmugs.entities.Artwork
import com.benjtissot.sellingmugs.entities.Mug
import io.ktor.util.logging.*
import react.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach


private val scope = MainScope()

val App = FC<Props> {
    val LOG = KtorSimpleLogger("App.kt")
    var mugList by useState(emptyList<Mug>())

    // At first initialisation, get the list
    // Alternative is useState when we want to persist something across re-renders
    useEffectOnce {
        scope.launch {
            mugList = getMugList()
        }
    }

    NavigationBarComponent {}



    MugListComponent {
        list = mugList
        onItemClick = {
            scope.launch {
                LOG.debug("Item ${it.name} clicked")
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
}