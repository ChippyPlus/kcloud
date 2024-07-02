package kcloud

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kcloud.plugins.*
import kcloud.routes.configureAIhRouting
import kcloud.routes.configureCryptRouting
import kcloud.routes.configureMathRouting
import kcloud.routes.configureStorageRouting

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
//    configureSecurity()
    configureHTTP()
    configureSerialization()
    configureRouting()
    configureMathRouting()
    configureStorageRouting()
    configureCryptRouting()
    configureAIhRouting()
}
