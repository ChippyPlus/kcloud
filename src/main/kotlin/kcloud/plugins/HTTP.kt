package kcloud.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.defaultheaders.*
import java.lang.Math.random

fun Application.configureHTTP() {
    install(DefaultHeaders) {
        header("X-Engine", "Ktor") // will send this header with each response
        header("Wants-to-chill-out", (listOf("true", "false").random()).toString())
    }
}
