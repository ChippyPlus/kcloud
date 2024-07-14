package kcloud.routes

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kcloud.kcloudHome
import java.io.File

data class UserDataInfoFormat(val json: Map<String, Any>) {
    val id: Int by json
    val password: String by json
    val name: String by json
    val allowed: Array<String> by json
}

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
            get("/admin/get-info/{user}") {
                val userTextData = File("$kcloudHome/auth/users.json").readText()
                val gson = Gson()
                val userAuthData: Map<String, Any?> =
                    gson.fromJson(userTextData, object : TypeToken<Map<String, Any?>>() {}.type)

                val allowedParams = arrayOf("id", "name", "password", "allowed")
                val user = call.parameters["user"]
                val newData = emptyMap<String, Any>().toMutableMap() // Use this if the client asks for specific prams
                val pr = emptyList<Any>().toMutableList()
                call.parameters.forEach { s, _ ->
                    if (s == "id") {
                        pr.add((s.toInt())) // to Turn into Int not Double
                    } else if (s != "user" && s in allowedParams) {
                        pr.add(s)
                    }
                }
                pr.forEach { thing ->
                    newData[thing.toString()] = (getUserInfo(userAuthData)[user] as Map<*, *>)[thing.toString()] as Any
                }
                println(getUserInfo(userAuthData)[user])
                if (pr.isEmpty()) {
                    call.respond(getUserInfo(userAuthData)[user] as Map<*, *>)
                } else {
                    call.respond(newData)
                }
            }
        }
        authenticate("basic-auth") {
            post("/admin/add-user/{user}") {
                val f =  File("$kcloudHome/auth/users.json")
                val userTextData = f.readText()
                val gson = Gson()
                val userAuthData: Map<String, Any?> =
                    gson.fromJson(userTextData, object : TypeToken<Map<String, Any?>>() {}.type)


                val user = call.parameters["user"]
                val body = call.receive<Map<String, Any>>()
                val wantedUserData = UserDataInfoFormat(body).json
                val newAuthData = userAuthData.toMutableMap()
                newAuthData[(user as Any).toString()] = wantedUserData
                val newNew = gson.toJson(newAuthData)
                f.writeText(newNew)
                call.respond(newAuthData)
            }


        }
    }
}