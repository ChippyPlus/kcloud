package kcloud.routes

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

@Suppress("unused")
fun Application.configureAdminRouting() {
    routing {
        authenticate("basic-auth") {

        }
    }
}