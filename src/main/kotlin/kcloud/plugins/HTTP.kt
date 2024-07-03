package kcloud.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.defaultheaders.*

fun Application.configureHTTP() {
    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // Sends this header with each response
        header("Wants-to-chill-out", (listOf("true", "false").random()).toString())
    }
}
