
val dash = "-".repeat(30)
fun java.util.logging.Logger?.delimit(message: String){
    this?.severe("$dash $message $dash")
}
