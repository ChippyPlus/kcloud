package kcloud

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kcloud.plugins.configureHTTP
import kcloud.plugins.configureRouting
import kcloud.plugins.configureSecurity
import kcloud.plugins.configureSerialization
import kcloud.routes.configureAIhRouting
import kcloud.routes.configureAdminRouting
import kcloud.routes.configureCryptRouting
import kcloud.routes.configureFunctionsRouting
import kcloud.routes.configureMathRouting
import kcloud.routes.configureStorageRouting
import kcloud.routes.configureTimeRouting
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

val kcloudPort = if (System.getenv("KCLOUD_PORT") == null) {
    2294
} else {
    System.getenv("KCLOUD_PORT").toInt()
}
var kcloudHome: String = System.getenv("KCLOUD_HOME") ?: "${System.getenv("HOME")}/.kcloud"
fun main() {
    println("NEW HOME => $kcloudHome, NEW PORT => $kcloudPort")
    val neededStartDirs = arrayOf(
        "$kcloudHome/", "$kcloudHome//logs", "$kcloudHome/static", "$kcloudHome/storage", "$kcloudHome/functions"
    )
    for (i in neededStartDirs) {
        if (!File(i).exists()) {
            File(i).mkdirs()
        }
    }
    embeddedServer(
        Netty, port = kcloudPort, host = "0.0.0.0", module = Application::module,
        watchPaths = listOf("classes", "libs"),
    ).start(wait = true)
}

fun Application.module() {
    configureSecurity()
    configureAdminRouting()
    configureSerialization()
    configureHTTP()
    configureRouting()
    /** System Routes */

    configureFunctionsRouting()
    configureTimeRouting()
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