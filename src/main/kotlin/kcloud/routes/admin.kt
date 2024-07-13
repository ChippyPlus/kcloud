package kcloud.routes

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kcloud.kcloudHome
import java.io.File

private val userTextData = File("$kcloudHome/auth/users.json").readText()
private val gson = Gson()
private val userAuthData: Map<String, Any?> =
    gson.fromJson(userTextData, object : TypeToken<Map<String, Any?>>() {}.type)

fun getUserInfo(userAuthData: Map<String, Any?>): Map<String, Any> {
    val usersInfoMap = emptyMap<String, Any>().toMutableMap()
    for (user in userAuthData) {
        val data = user.value as Map<*, *>
        usersInfoMap[user.key] = data
    }
    return usersInfoMap.toMap()
}

fun Application.configureAdminRouting() {
    routing {
        authenticate("basic-auth") {
            post("/admin/get-info/{user}") {
                val user = call.parameters["user"]
                val pr = emptyList<Any>().toMutableList()
                call.parameters.forEach { s, _ -> if (s != "user") pr.add(s) }
                call.respond(mapOf("data" to getUserInfo(userAuthData)[user], "pr" to pr))
            }
        }
    }
}