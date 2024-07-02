package kcloud.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import sun.security.util.KeyUtil.validate

fun Application.configureSecurity() {
    install(Authentication) {
        basic("auth-basic") {
            realm = "Access to the '/' path"
            validate { credentials ->
                if (credentials.name == "admin" && credentials.password == "foobar") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
}

