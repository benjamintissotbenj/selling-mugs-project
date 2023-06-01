package com.benjtissot.sellingmugs.pages

import com.benjtissot.sellingmugs.*
import com.benjtissot.sellingmugs.components.CreateProductComponent
import com.benjtissot.sellingmugs.components.FooterComponent
import com.benjtissot.sellingmugs.components.LogoutButtonComponent
import com.benjtissot.sellingmugs.components.NavigationBarComponent
import csstype.Display
import emotion.react.css
import io.ktor.http.*
import io.ktor.util.logging.*
import kotlinx.coroutines.launch
import mui.icons.material.AddCircle
import mui.icons.material.Refresh
import mui.material.IconButton
import react.FC
import react.dom.html.ReactHTML.div
import react.router.useNavigate
import react.useEffectOnce
import react.useState

private val LOG = KtorSimpleLogger("AdminPanelPage.kt")

external interface AdminPanelPageProps : SessionPageProps {
}

val AdminPanelPage = FC<SessionPageProps> { props ->

    val navigateAdmin = useNavigate()
    var message by useState("")
    useEffectOnce {
        scope.launch {
            message = getUserInfo()
        }
    }
    var createProduct by useState(false)

    if ((props.session.user?.userType ?: Const.UserType.CLIENT) == Const.UserType.ADMIN){
        div {
            css {
                mainPageDiv()
            }

            div {
                css {
                    fontNormal()
                }
                +"Hello Admin Page"
                +"Extra message $message"
            }

            if (!createProduct){
                IconButton {
                    div {
                        +"Create Product"
                    }
                    AddCircle()
                    onClick = {
                        createProduct = true
                    }
                }
            } else {
                CreateProductComponent{

                }
            }

            LogoutButtonComponent {
                session = props.session
                updateSession = props.updateSession
                navigate = navigateAdmin
            }
        }
    } else {
        div {
            divDefaultCss()
            +"You must be an admin to view this page"
        }
    }
}
