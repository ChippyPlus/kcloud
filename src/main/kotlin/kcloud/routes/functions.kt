package kcloud.routes


import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kcloud.kcloudHome
import kcloud.log
import java.io.File
import kotlin.collections.set


fun Application.configureFunctionsRouting() {
    val processes = emptyMap<String, Int>().toMutableMap()
    routing {
        authenticate("basic-auth") {
            post("/functions/activate") {

                val what = call.receive<Map<String, String>>()["arg1"].toString()
                val pid = Runtime.getRuntime().exec("$kcloudHome/functions/$what").pid().toInt()
                processes[what] = pid //                println("called WHAT=\"$what\"")
                call.response.status(HttpStatusCode.NoContent)

            }
            delete("/functions/deactivate") {
                val what = call.receive<Map<String, String>>()["arg1"] // function name
                val processPID = processes[what]
                if (processPID == null) {
                    call.respond(
                        message = mapOf("error" to "process-not-found", "requested-process" to what),
                        status = HttpStatusCode.NotFound
                    )
                    return@delete // To make sure no more code gets executed.
                }

                Runtime.getRuntime().exec("pkill -9 -P $processPID")
                call.respond(message = mapOf("message" to "terminated", "pid" to processPID))
                processes.remove(what) // Removed $what from the processes map. So the server forgets about it.
            }
        }
        post("/functions/upload") {
            var fileName: String
            val multipartData = call.receiveMultipart() // load as a file

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        fileName = part.originalFileName as String
                        val fileBytes = part.streamProvider().readBytes()
                        File("$kcloudHome/functions/$fileName").writeBytes(fileBytes)
                        call.respond(
                            mapOf(
                                "message" to "Uploaded", "location" to fileName,
                                "real" to "$kcloudHome/functions/$fileName"
                            )
                        )
                        log(
                            "functions", "/functions/upload", "Downloaded \"$fileName\""
                        )
                        log(whereDidItHappen = "/functions/upload")
                        return@forEachPart
                    }

                    else -> {}
                }
                part.dispose()
            }
            call.respond(message = mapOf("error" to "gone-to-far"), status = HttpStatusCode.InternalServerError)
            log("functions", "/functions/upload", "ERROR | 500 | gone-to-far")
        }
        get("/functions/download") {

            val arg1 = call.receive<Map<String, String>>()["arg1"]!!
            val f = File("src/main/resources/functions/${arg1}")

            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, f.name).toString()
            )
            call.respondFile(f)
            log("functions", "/functions/download", "Uploaded \"$arg1\"")
        }


    }
}
