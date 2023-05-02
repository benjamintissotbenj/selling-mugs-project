import react.*
import kotlinx.coroutines.*
import react.dom.html.ReactHTML.h1
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.ul

private val scope = MainScope()

val App = FC<Props> {
    var mugList by useState(emptyList<MugListItem>())

    // At first initialisation, get the list
    // Alternative is useState when we want to persist something across re-renders
    useEffectOnce {
        scope.launch {
            mugList = getMugList()
        }
    }

    // Creates a h1 component
    h1 {
        +"First steps of the Selling Mugs project"
    }
    // Creates a ulist component
    ul {
        mugList.sortedByDescending(MugListItem::priority).forEach { item ->
            li {
                key = item.toString()
                +"[${item.priority}] ${item.desc} "
                onClick = {
                    scope.launch {
                        deleteMugListItem(item) // deletes from server
                        mugList = getMugList() // updates client
                    }
                }
            }
        }
    }
    // Creating a field to input a new element
    inputComponent {
        onSubmit = { input ->
            val cartItem = MugListItem(input.replace("!", ""), input.count { it == '!' })
            scope.launch {
                addMugListItem(cartItem)
                mugList = getMugList() // updates the state (using "useState") so re-renders page
            }
        }
    }
}