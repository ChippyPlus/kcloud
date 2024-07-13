package kcloud.routes


import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun checkAISupport(): Boolean = System.getProperty("os.arch") != "armv61"


fun Application.configureAIhRouting() {
    routing {


        authenticate("basic-auth-AI/GENERATE") {
            post("/ai/ollama/generate") {
                if (!checkAISupport()) {
                    call.respond(message = mapOf("error" to "BadArch"), status = HttpStatusCode.InternalServerError)
                    return@post
                }
                val client = HttpClient(CIO) {
                    engine {
                        requestTimeout = 0
                    }
                    install(ContentNegotiation) {
                        gson {}
                        install(HttpTimeout) {}
                    }
                }

                val response: HttpResponse =
                    client.post("http://localhost:11434/api/generate?model=gemma:2b&prompt=hello") {
                        contentType(ContentType.Application.Json)
                        val requestBody = call.receive<Map<String, String>>()
                        setBody(
                            mapOf(
                                "model" to requestBody["arg1"], "prompt" to requestBody["arg2"], "stream" to false
                            )
                        )
                    }
                val body = response.body<String>()
                println(body)
                call.response.headers.append("Content-Type", "application/json")
                call.respond(body)
                client.close()
            }
        }
    }
}



