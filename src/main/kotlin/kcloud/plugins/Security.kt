package kcloud.plugins

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.util.*
import kcloud.kcloudHome
import java.io.File


val digestFunction = getDigestFunction("SHA-256") { "ktor${it.length}" }




fun getUsersAndPasswords(userAuthData: Map<String, Any?>): Map<String, ByteArray> {
    val usersAndPasswords = emptyMap<String, ByteArray>().toMutableMap()
    for (user in userAuthData) {
        val data = user.value as Map<*, *>
        usersAndPasswords[user.key] = digestFunction(data["password"].toString())
        println("USER: ${user.key} | USERDATA: $data")
    }
    return usersAndPasswords.toMap()
}

fun getUserInfo(userAuthData: Map<String, Any>): Map<String, Any> {
    val usersAndPasswords = emptyMap<String, Any>().toMutableMap()
    for (user in userAuthData) {
        val data = user.value as Map<*, *>
        usersAndPasswords[user.key] = data
    }
    return usersAndPasswords.toMap()
}


fun Application.configureSecurity() {
    install(Authentication) {
        basic("basic-auth") {
            validate { credentials ->
                val userTextData = File("$kcloudHome/auth/users.json").readText()
                val gson = Gson()
                val userAuthData: Map<String, Any?> =
                    gson.fromJson(userTextData, object : TypeToken<Map<String, Any?>>() {}.type)
                val hashedUserTable = UserHashedTableAuth(
                    table = getUsersAndPasswords(userAuthData), digester = digestFunction
                )
                println("working!!!!!!!!!!!!!!!!!!!")
                hashedUserTable.authenticate(credentials)
            }
        }
    }
}

