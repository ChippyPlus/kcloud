package kcloud.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.routing.*
import kotlinx.html.*

fun Application.configureRouting() {
    routing {
        get("/") {
            val name = "Ktor"
            call.respondHtml(HttpStatusCode.OK) {
                head {
                    title {
                        +name
                    }
                }
                body {
                    h1 {
                        + "Hello from $name!!!!!"
                    }
                    button {
                        +"click me"
                        attributes["oneClick"] = "alert('Hello!');"
                    }
                }
            }

        }
        get("/html-dsl/login") {
            call.respondHtml {
                body {
                    form(action = "/html-dsl/login", encType = FormEncType.applicationXWwwFormUrlEncoded, method = FormMethod.post) {
                        p {
                            +"Username:"
                            textInput(name = "username")
                        }
                        p {
                            +"Password:"
                            textInput(name = "password")
                        }
                        p {
                            submitInput {value= "login"}
                        }
                    }
                }
            }
        }
    }
}

