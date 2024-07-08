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

fun Application.configureStorageRouting() {
    routing {
        authenticate("basic-auth-STORAGE/DOWNLOAD") {
            get("/storage/download") {

                val arg1 = call.receive<Map<String, String>>()["arg1"]!!
                val f = File("$kcloudHome/storage/${arg1}")

                call.response.header(
                    HttpHeaders.ContentDisposition,
                    ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, f.name)
                        .toString()
                )
                call.respondFile(f)
                log("storage", "storage/download", "downloaded \"$arg1\"")
                log(whereDidItHappen = "storage/download")
            }
        }
        authenticate("basic-auth-STORAGE/UPLOAD") {
            post("/storage/upload") {
                var fileName = ""
                val multipartData = call.receiveMultipart()
                val filePath = "$kcloudHome/storage/$fileName"
                multipartData.forEachPart { part ->
                    when (part) {
                        is PartData.FileItem -> {
                            fileName = part.originalFileName as String
                            val fileBytes = part.streamProvider().readBytes()

                            File("$filePath/$fileName").writeBytes(fileBytes)
                        }

                        else -> {}
                    }
                    part.dispose()
                }
                call.respond(
                    status = HttpStatusCode.OK, message = mapOf(
                        "message" to "uploaded", "location" to fileName, "real" to filePath
                    )
                )
                log("storage", "storage/upload", "uploaded \"$fileName\"")
                log("tasks", "storage/upload", "uploaded \"$fileName\"")
            }
        }
    }
}
