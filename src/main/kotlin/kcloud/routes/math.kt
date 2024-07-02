package kcloud.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.math.pow

fun Application.configureMathRouting() {
    routing {
        authenticate("basic-auth") {
            post("/math/{operation}") {


                val body = call.receive<Map<String, Int>>()
                val arg1 = body["arg1"]!!
                val arg2 = body["arg2"]!!

                if (call.parameters["operation"] == "add") {
                    call.respond(mapOf("message" to (arg1 + arg2)))
                } else if (call.parameters["operation"] == "sub") {
                    call.respond(mapOf("message" to (arg1 - arg2)))
                } else if (call.parameters["operation"] == "mul") {
                    call.respond(mapOf("message" to (arg1 * arg2)))
                } else if (call.parameters["operation"] == "div") {
                    call.respond(mapOf("message" to (arg1 / arg2)))
                } else if (call.parameters["operation"] == "pow") {
                    call.respond(mapOf("message" to (arg1.toDouble().pow(arg2))))
                } else if (call.parameters["operation"] == "mod") {
                    call.respond(mapOf("message" to (arg1 % arg2)))
                } else {
                    call.respond(status = HttpStatusCode.NotFound, mapOf("error" to "NoMathOp"))
                }
            }
        }
    }
}