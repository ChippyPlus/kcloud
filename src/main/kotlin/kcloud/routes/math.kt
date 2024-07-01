package kcloud.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlin.math.pow

fun Application.configureMathRouting() {
    routing {
        post("/math/{operation}") {

            /** to check if mandatory arguments are missing*/
            if ("arg1" !in call.parameters.names()) {
                call.respond(status = HttpStatusCode.BadRequest, message = mapOf("error" to "arg1"))
            } else if ("arg2" !in call.parameters.names()) {
                call.respond(status = HttpStatusCode.BadRequest, message = mapOf("error" to "arg2"))
            }

            val arg1 = call.parameters["arg1"]!!
            val arg2 = call.parameters["arg2"]!!
            if (call.parameters["operation"] == "add") {
                call.respond(mapOf("message" to (arg1.toInt() + arg2.toInt())))
            } else if (call.parameters["operation"] == "sub") {
                call.respond(mapOf("message" to (arg1.toInt() - arg2.toInt())))
            } else if (call.parameters["operation"] == "mul") {
                call.respond(mapOf("message" to (arg1.toInt() * arg2.toInt())))
            } else if (call.parameters["operation"] == "div") {
                call.respond(mapOf("message" to (arg1.toInt() / arg2.toInt())))
            } else if (call.parameters["operation"] == "pow") {
                call.respond(mapOf("message" to (arg1.toDouble().pow(arg2.toDouble()))))
            } else if (call.parameters["operation"] == "mod") {
                call.respond(mapOf("message" to (arg1.toInt() % arg2.toInt())))
            } else {
                call.respond(status = HttpStatusCode.NotFound, mapOf("error" to "NoMathOp"))
            }
        }
    }
}