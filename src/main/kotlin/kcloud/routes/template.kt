package kcloud.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

@Suppress("unused")
fun Application.configureCHANGEMERouting() {
    routing {
        authenticate("basic-auth") {

        }
    }
}