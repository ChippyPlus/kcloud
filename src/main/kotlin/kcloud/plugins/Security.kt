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
    val usersInfoMap = emptyMap<String, Any>().toMutableMap()
    for (user in userAuthData) {
        val data = user.value as Map<*, *>
        usersInfoMap[user.key] = data
    }
    return usersInfoMap.toMap()
}

fun check(items: Array<String>): MutableMap<String, ByteArray> {
    val userTextData = File("$kcloudHome/auth/users.json").readText()
    val gson = Gson()
    val userAuthData: Map<String, Any?> = gson.fromJson(userTextData, object : TypeToken<Map<String, Any?>>() {}.type)
    val usersAndPasswords = emptyMap<String, ByteArray>().toMutableMap()
    for (user in userAuthData) {
        val data = (user.value as Map<*, *>).toMutableMap()
        for (perm in items) {
            if (perm in data["allowed"] as List<*> || "ALL" in data["allowed"] as List<*>) {
                usersAndPasswords[user.key] = digestFunction(data["password"].toString())
            }
        }
    }
    return usersAndPasswords
}


fun Application.configureSecurity() {
    install(Authentication) {
        basic("basic-auth") {
            validate { credentials ->
                val userTextData = File("$kcloudHome/auth/users.json").readText()
                val gson = Gson()
                val userAuthData: Map<String, Any?> =
                    gson.fromJson(userTextData, object : TypeToken<Map<String, Any?>>() {}.type)
                val hashedUserTable =
                    UserHashedTableAuth(table = getUsersAndPasswords(userAuthData), digester = digestFunction)
                hashedUserTable.authenticate(credentials)
            }
        }
        basic("basic-auth-MATH") {
            validate { credentials ->
                val usersAndPasswords: MutableMap<String, ByteArray> = check(arrayOf("MAT"))
                val hashedUserTable = UserHashedTableAuth(table = usersAndPasswords, digester = digestFunction)
                hashedUserTable.authenticate(credentials)
            }
        }

        basic("basic-auth-STORAGE/UPLOAD") {
            validate { credentials ->
                val usersAndPasswords: MutableMap<String, ByteArray> = check(
                    arrayOf(
                        "STU", // STorage Upload
                        "GEU", // GEneric Upload
                        "STA"  // STorage All
                    )
                )
                val hashedUserTable = UserHashedTableAuth(table = usersAndPasswords, digester = digestFunction)
                hashedUserTable.authenticate(credentials)
            }
        }
        basic("basic-auth-STORAGE/DOWNLOAD") {
            validate { credentials ->
                UserHashedTableAuth(
                    table = check(
                        arrayOf(
                            "STD", "GED", "STA"
                        )
                    ), digester = digestFunction
                ).authenticate(credentials)
            }
        }
        basic("basic-auth-functions/upload") {
            validate { credentials ->
                UserHashedTableAuth(
                    table = check(
                        arrayOf(
                            "FUU", "GEU", "FUA"
                        )
                    ), digester = digestFunction
                ).authenticate(credentials)
            }
        }
        basic("basic-auth-functions/download") {
            validate { credentials ->
                UserHashedTableAuth(
                    table = check(
                        arrayOf(
                            "FUD", "GED", "FUA"
                        )
                    ), digester = digestFunction
                ).authenticate(credentials)
            }
        }
        TODO("add more for all endpoints then apply")
    }
}


