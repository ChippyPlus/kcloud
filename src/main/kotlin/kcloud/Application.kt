package kcloud

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kcloud.plugins.configureHTTP
import kcloud.plugins.configureRouting
import kcloud.plugins.configureSecurity
import kcloud.plugins.configureSerialization
import kcloud.routes.configureAIhRouting
import kcloud.routes.configureCryptRouting
import kcloud.routes.configureFunctionsRouting
import kcloud.routes.configureMathRouting
import kcloud.routes.configureStorageRouting
import kcloud.routes.configureTimeRouting
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val kcloudHome: String = System.getenv("KCLOUD_HOME") ?: "/${System.getenv("HOME")}/.kcloud"
fun main() {
    val neededStartDirs = arrayOf(
        "$kcloudHome/", "$kcloudHome//logs", "$kcloudHome/static", "$kcloudHome/storage", "$kcloudHome/functions"
    )

    for (i in neededStartDirs) {

        if (!File(i).exists()) {
            File(i).mkdirs()
            println("created $i")
        }
    }



    embeddedServer(
        Netty, port = 8080, host = "localhost", module = Application::module, watchPaths = listOf("src/main")
    ).start(wait = true)
}

fun Application.module() {
    System.setProperty("io.ktor.development", "true")
    configureSecurity()
    configureFunctionsRouting()
    configureTimeRouting()
    configureHTTP()
    configureSerialization()
    configureRouting()
    configureMathRouting()
    configureStorageRouting()
    configureCryptRouting()
    configureAIhRouting()
}


fun log(fileNameWithOutExt: String = "tasks", whereDidItHappen: String, content: String = "executed") {
    val f = File("${kcloudHome}/logs/$fileNameWithOutExt.log")
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("HH:mm:ss, EEEE MMMM d, yyyy") // Customize the format here
    val formattedTime = current.format(formatter)
    if (f.exists()) {
        f.appendText("[$formattedTime] $whereDidItHappen | $content\n")
    } else {
        f.createNewFile()
        f.appendText("[$formattedTime] $whereDidItHappen | $content\n")
    }
}