package kcloud.routes

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kcloud.log
import kotlin.math.pow


fun mathLog(where: String, content: String) {
    log(fileNameWithOutExt = "math", whereDidItHappen = "math/$where", content = content)
    log(whereDidItHappen = where)
}


fun Double.roundIfInt(): Any {
    return if (this == this.toInt().toDouble()) {
        this.toInt()
    } else {
        this
    }
}



fun Application.configureMathRouting() {
    routing {
        authenticate("basic-auth") {

            post("/math/{operation}") {

                val body = call.receive<Map<String, Any>>()
                var arg1 =
                    body["arg1"] ?: return@post call.respondText(text = "arg1", status = HttpStatusCode.BadRequest)
                var arg2 =
                    body["arg2"] ?: return@post call.respondText(text = "arg2", status = HttpStatusCode.BadRequest)

                try {

                    arg1 = arg1 as Double
                    arg2 = arg2 as Double
                } catch (numberFormatError: ClassCastException) {
                    return@post call.respondText(text = "numberFormatError", status = HttpStatusCode.BadRequest)
                }

                when (call.parameters["operation"]) {
                    "add" -> {
                        val answer= arg1 + arg2
                        call.respond(mapOf("message" to answer.roundIfInt().toString()))
                        mathLog("add", "added")
                    }

                    "sub" -> {
                        val answer = arg1 - arg2
                        call.respond(mapOf("message" to answer.roundIfInt().toString()))
                        mathLog("sub", "subtracted")
                    }

                    "mul" -> {
                        val answer = arg1 * arg2
                        call.respond(mapOf("message" to answer.roundIfInt().toString()))
                        mathLog("mul", "multiply")
                    }

                    "div" -> {
                        val answer = arg1 / arg2
                        call.respond(mapOf("message" to answer.roundIfInt().toString()))
                        mathLog("div", "divided")
                    }

                    "pow" -> {
                        val answer = arg1.toDouble().pow(arg2)
                        call.respond(mapOf("message" to answer.roundIfInt().toString()))
                        mathLog("pow", "powered")
                    }

                    "mod" -> {
                        val answer = arg1 % arg2
                        call.respond(mapOf("message" to answer.roundIfInt().toString()))
                        mathLog("mod", "modulus-ed")
                    }
                    // TODO add math/factorial


                    else -> {
                        call.respond(message = "NoMathOp", status = HttpStatusCode.NotFound)
                    }
                }

            }
        }
    }
}