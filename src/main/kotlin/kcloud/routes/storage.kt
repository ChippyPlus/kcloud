package kcloud.routes

import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Application.configureStorageRouting() {
    routing {
        get("/storage/download") {

            /** to check if mandatory arguments are missing*/
            val arg1 = call.receive<Map<String, String>>()["arg1"]!!
            val f = File("src/main/resources/storage/${arg1}")

            /** Tell the client that we are sending a file*/
            call.response.header(
                HttpHeaders.ContentDisposition,
                ContentDisposition.Attachment.withParameter(ContentDisposition.Parameters.FileName, f.name)
                    .toString()
            )
            call.respondFile(f)
        }
        post("/storage/upload") {
            var fileName: String
            val multipartData = call.receiveMultipart()

            multipartData.forEachPart { part ->
                when (part) {
                    is PartData.FileItem -> {
                        fileName = part.originalFileName as String
                        val fileBytes = part.streamProvider().readBytes()
                        File("src/main/resources/storage//$fileName").writeBytes(fileBytes)
                    }

                    else -> {}
                }
                part.dispose()
            }
            call.respond(mapOf("message" to "Uploaded"))
        }
    }
}
