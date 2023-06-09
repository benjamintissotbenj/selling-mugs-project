
val dash = "-".repeat(30)
fun io.ktor.util.logging.Logger?.delimit(message: String){
    this?.error("$dash $message $dash")
}
