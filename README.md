# Project "Selling Mugs"

Welcome to the codebase for the Selling Mugs project by Benjamin Tissot for the M.Sc. Computing (Software Engineering) Master's Thesis at Imperial College London.

This file will explain how the code is organised and will give you the different relevant links. You can find the production version of this website [hosted by Heroku](https://selling-mugs-f3318fd8d6c6.herokuapp.com/) until mid-september, at which point the website will be shut down as it will not be used for commercial purposes.

## Project Structure

This project is a [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform-get-started.html) full-stack webapp, meaning that Kotlin was the main language for both front-end and back-end development. This project relies on Kotlin/JVM for the back-end, and on Kotlin/JS - Kotlin/React for the front-end. Last but not least, this project makes use of environment variables, which will be described in the appropriate sections below, as well as summarised in a table below.

### Backend
##### Routing
The routing for this project is made using [Ktor](https://ktor.io/docs/welcome.html). Most of the content is served statically - since this is a webapp -, but in an effort to enable refreshing to redirect to the correct part of the website, all the front-end render routes have been added as redirections in the back-end.
The code makes use of the `HOST` and `PORT` environment variables for clean deployment.

##### Database
This project relies on [MongoDB Atlas](https://www.mongodb.com/atlas/database) for storing its databases. The interaction with MongoDb is made with [KMongo](https://litote.org/kmongo/).
The code uses `MONGODB_URI` and `MONGODB_DBNAME` as environment variables to determine respectively the URI of the cloud server to connect to, and the name of the database to connect to in that server.

##### APIs
This project is made of many connections to external APIs. It connects to [Printify](https://printify.com/) to manage custom print-on-demand for the users. The payments are managed securely thanks to [Stripe](https://stripe.com/en-gb).
The code uses the `PRINTIFY_STORE_ID`, `STRIPE_API_KEY_REAL`, `STRIPE_API_KEY_TEST`, `STRIPE_WEBHOOK_SECRET_REAL` and `STRIPE_WEBHOOK_SECRET_TEST` environment variables to manage the connection to the [Printify](https://printify.com/) and [Stripe](https://stripe.com/en-gb) APIs.


### Frontend

Kotlin/React is a very powerful tool, but it is rather new. This means there aren't many good quality wrappers for every React library. However, a couple of them are available and allow for a decent quality front-end.

##### React Routing
Luckily for us, Jetbrains have developed some Kotlin wrappers for the most popular libraries. This includes [React Router](https://github.com/JetBrains/kotlin-wrappers/tree/master/kotlin-react-router-dom) which allows us to navigate within our web-app (served statically)

##### Material UI
Another external library adapted to Kotlin by Jetbrains is [Material UI](https://mui.com/material-ui/getting-started/) ([Wrapper](https://github.com/JetBrains/kotlin-wrappers/tree/master/kotlin-mui)), from which almost every UI element in the website comes from (Buttons, Icons, Tabs...).

##### Ring UI
The only element from Material UI that was found to be difficult to handle was the Grid, but the one from [Ring UI](https://github.com/JetBrains/ring-ui) ([Wrapper](https://github.com/JetBrains/kotlin-wrappers/tree/master/kotlin-ring-ui)) was a breeze to use.


### Common code

The whole point of having a kotlin multiplatform project is to be able to share code between different platforms. In this case, we are sharing some code between the back-end and the front-end, mainly the different entities used.
The common package is also where all the static files are stored (under `commonMain/resources`)


### Tests
As of now, this project has only a small percentage of test coverage. The tests exclusively concern the JVM part, as it is where most (if not all) of the data management happens, and where the different API calls are handled.
On a side note, the connection to Stripe uses webhooks to work, which means any Stripe testing needs to run with a local redirection running in the command prompt. More information [here](https://stripe.com/docs/webhooks/test).