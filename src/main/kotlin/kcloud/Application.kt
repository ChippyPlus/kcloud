package kcloud

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kcloud.plugins.configureHTTP
import kcloud.plugins.configureRouting
import kcloud.plugins.configureSecurity
import kcloud.plugins.configureSerialization
import kcloud.routes.configureAIhRouting
import kcloud.routes.configureCryptRouting
import kcloud.routes.configureFunctionsRouting
import kcloud.routes.configureMathRouting
import kcloud.routes.configureStorageRouting
import kcloud.routes.configureTimeRouting

fun main() {
    embeddedServer(
        Netty, port = 8080, host = "localhost", module = Application::module, watchPaths = listOf("src/main")
    ).start(wait = true)
}

fun Application.module() {
    System.setProperty("io.ktor.development", "true")
    configureSecurity()
    configureFunctionsRouting()
    configureTimeRouting()
    configureHTTP()
    configureSerialization()
    configureRouting()
    configureMathRouting()
    configureStorageRouting()
    configureCryptRouting()
    configureAIhRouting()
}
