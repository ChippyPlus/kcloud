package kcloud.routes


import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kcloud.kcloudHome
import java.io.File
import java.io.IOException
import kotlin.collections.set


fun Application.configureFunctionsRouting() {
    val processes = emptyMap<String, Int>().toMutableMap()
    routing {
        authenticate("basic-auth") {
            post("/functions/activate") {

                val what = call.parameters["a"]!!
                val pid = Runtime.getRuntime().exec("$kcloudHome/functionStorage/$what").pid().toInt()
                processes[what] = pid
                println("called WHAT=\"$what\"")
                call.response.status(HttpStatusCode.NoContent)

            }
            delete("/functions/deactivate") {
                val what = call.parameters["a"]
                if (processes[what] == null) {
                    call.response.status(HttpStatusCode.NotFound)
                    return@delete // To make sure no more code gets executed.
                }
                try {
                    val code = Runtime.getRuntime().exec("pkill -9 -P ${processes[what]}").exitValue()
                    if (code != 0) {
                        call.respond(
                            message = mapOf("error" to "ShellError"), status = HttpStatusCode.InternalServerError
                        )
                        return@delete
                    }
                } catch (ioError: IOException) {
                    call.respond(message = mapOf("error" to "NoFoundPID"), status = HttpStatusCode.NotFound)
                }

                call.response.status(HttpStatusCode.NoContent)


            }
        }
        post("/functions/upload") {
            var fileName: String
            val multipartData = call.receiveMultipart()

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        fileName = part.originalFileName as String
                        val fileBytes = part.streamProvider().readBytes()
                        File("src/main/resources/functions/$fileName").writeBytes(fileBytes)
                    }

                    else -> {}
                }
                part.dispose()
            }
            call.respond(mapOf("message" to "Uploaded"))

        }
        get("/functions/download") {

            val arg1 = call.receive<Map<String, String>>()["arg1"]!!
            val f = File("src/main/resources/functions/${arg1}")

            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, f.name).toString()
            )
            call.respondFile(f)
        }


    }
}
