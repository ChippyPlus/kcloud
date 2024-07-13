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
        val endpointNameForLog = "functions"
        authenticate("basic-auth-FUNCTIONS/ACTIVATE") {
            val subEndpointName = "/functions/activate"
            post(subEndpointName) {

                val what = call.receive<Map<String, String>>()["arg1"].toString()
                val pid = Runtime.getRuntime().exec("$kcloudHome/$endpointNameForLog/$what").pid().toInt()
                processes[what] = pid
                call.response.status(HttpStatusCode.NoContent)
                log(endpointNameForLog, subEndpointName, "activated \"$what\"")
                log(whereDidItHappen = subEndpointName)

            }
        }
        authenticate("basic-auth-FUNCTIONS/DEACTIVATE") {
            val subEndpointName = "/functions/deactivate"
            delete(subEndpointName) {
                val what = call.receive<Map<String, String>>()["arg1"] // function name
                val processPID = processes[what]
                if (processPID == null) {
                    call.respond(
                        message = mapOf("error" to "process-not-found", "requested-process" to what),
                        status = HttpStatusCode.NotFound
                    )
                    log(endpointNameForLog, subEndpointName, "process not found \"$what\"")
                    return@delete // To make sure no more code gets executed.
                }


                Runtime.getRuntime().exec("pkill -9 -P $processPID")
                call.respond(message = mapOf("message" to "terminated", "pid" to processPID))
                processes.remove(what) // Removed $what from the processes map. So the server forgets about it.
                log(endpointNameForLog, subEndpointName, "terminated \"$what\"($processPID)")
                log(whereDidItHappen = subEndpointName)
            }
        }
        authenticate("basic-auth-FUNCTIONS/UPLOAD") {
            val subEndpointNameForLog = "functions/upload"
            post(subEndpointNameForLog) {
                var fileName: String
                val multipartData = call.receiveMultipart() // load as a file

                multipartData.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            fileName = part.originalFileName as String
                            val fileBytes = part.streamProvider().readBytes()
                            File("$kcloudHome/$endpointNameForLog/$fileName").writeBytes(fileBytes)
                            call.respond(
                                mapOf(
                                    "message" to "Uploaded", "location" to fileName,
                                    "real" to "$kcloudHome/$endpointNameForLog/$fileName"
                                )
                            )
                            log(endpointNameForLog, subEndpointNameForLog, "Downloaded \"$fileName\"")
                            log(whereDidItHappen = subEndpointNameForLog)
                            return@forEachPart
                        }

                        else -> {}
                    }
                    part.dispose()
                }
                call.respond(message = mapOf("error" to "gone-to-far"), status = HttpStatusCode.InternalServerError)
                log(endpointNameForLog, subEndpointNameForLog, "ERROR | 500 | gone-to-far")
            }
        }
        authenticate("basic-auth-FUNCTION/DOWNLOAD") {
            val subEndPointName = "/functions/download"
            get(subEndPointName) {

                val arg1 = call.receive<Map<String, String>>()["arg1"]!!
                val f = File("src/main/resources/$endpointNameForLog/${arg1}")

                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, f.name)
                        .toString()
                )
                call.respondFile(f)
                log(endpointNameForLog, subEndPointName, "Uploaded \"$arg1\"")
                log(whereDidItHappen = subEndPointName)
            }


        }
    }
}