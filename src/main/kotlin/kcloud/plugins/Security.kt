package kcloud.plugins

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.util.*
import kcloud.constants.Endpoints
import kcloud.constants.Privileges
import kcloud.constants.getPrivilege
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
    val allPrivileges = Privileges()
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
                val usersAndPasswords: MutableMap<String, ByteArray> =
                    check(allPrivileges.getPrivilege(Endpoints.MathAny))
                val hashedUserTable = UserHashedTableAuth(table = usersAndPasswords, digester = digestFunction)
                hashedUserTable.authenticate(credentials)
            }
        }
        basic("basic-auth-STORAGE/UPLOAD") {
            validate { credentials ->
                val usersAndPasswords: MutableMap<String, ByteArray> =
                    check(allPrivileges.getPrivilege(Endpoints.StorageUpload))
                val hashedUserTable = UserHashedTableAuth(table = usersAndPasswords, digester = digestFunction)
                hashedUserTable.authenticate(credentials)
            }
        }
        basic("basic-auth-STORAGE/DOWNLOAD") {
            validate { credentials ->
                val usersAndPasswords: MutableMap<String, ByteArray> =
                    check(allPrivileges.getPrivilege(Endpoints.StorageDownload))
                UserHashedTableAuth(table = usersAndPasswords, digester = digestFunction).authenticate(credentials)
            }
        }
        basic("basic-auth-FUNCTION/UPLOAD") {
            validate { credentials ->
                val usersAndPasswords: MutableMap<String, ByteArray> = check(allPrivileges.getPrivilege(Endpoints.FunctionUpload))
                UserHashedTableAuth(table = usersAndPasswords, digester = digestFunction).authenticate(credentials)
            }
        }
        basic("basic-auth-FUNCTION/DOWNLOAD") {
            validate { credentials ->
                val usersAndPasswords: MutableMap<String, ByteArray> = check(allPrivileges.getPrivilege(Endpoints.FunctionDownload))
                UserHashedTableAuth(table = usersAndPasswords, digester = digestFunction).authenticate(credentials)
            }
        }
        basic("basic-auth-FUNCTION/ACTIVATE") {
            validate { credentials ->
                val usersAndPasswords: MutableMap<String, ByteArray> = check(allPrivileges.getPrivilege(Endpoints.FunctionActivate))
                UserHashedTableAuth(table = usersAndPasswords, digester = digestFunction).authenticate(credentials)
            }
        }
        basic("basic-auth-FUNCTION/DEACTIVATE") {
            validate { credentials ->
                val usersAndPasswords: MutableMap<String, ByteArray> = check(allPrivileges.getPrivilege(Endpoints.FunctionDeactivate))
                UserHashedTableAuth(table = usersAndPasswords, digester = digestFunction).authenticate(credentials)
            }
        }
        basic("basic-auth-AI/GENERATE") {
            validate { credentials ->
                val usersAndPasswords: MutableMap<String, ByteArray> = check(allPrivileges.getPrivilege(Endpoints.AiGenerate))
                UserHashedTableAuth(table = usersAndPasswords, digester = digestFunction).authenticate(credentials)
            }
        }
        basic("basic-auth-CRYPT/KEYGENERATE") {
            validate { credentials ->
                val usersAndPasswords: MutableMap<String, ByteArray> = check(allPrivileges.getPrivilege(Endpoints.CryptKeyGen))
                UserHashedTableAuth(table = usersAndPasswords, digester = digestFunction).authenticate(credentials)
            }
        }
        basic("basic-auth-CRYPT/ENCRYPT") {
            validate { credentials ->
                val usersAndPasswords: MutableMap<String, ByteArray> = check(allPrivileges.getPrivilege(Endpoints.CryptEncrypt))
                UserHashedTableAuth(table = usersAndPasswords, digester = digestFunction).authenticate(credentials)
            }
        }
        basic("basic-auth-CRYPT/DECRYPT") {
            validate { credentials ->
                val usersAndPasswords: MutableMap<String, ByteArray> = check(allPrivileges.getPrivilege(Endpoints.CryptDecrypt))
                UserHashedTableAuth(table = usersAndPasswords, digester = digestFunction).authenticate(credentials)
            }
        }
        basic("basic-auth-TIME/GET") {
            validate { credentials ->
                val usersAndPasswords: MutableMap<String, ByteArray> = check(allPrivileges.getPrivilege(Endpoints.TimeGet))
                UserHashedTableAuth(table = usersAndPasswords, digester = digestFunction).authenticate(credentials)
            }
        }
        basic("basic-auth-TIME/SET") {
            validate { credentials ->
                val usersAndPasswords: MutableMap<String, ByteArray> = check(allPrivileges.getPrivilege(Endpoints.TimeSet))
                UserHashedTableAuth(table = usersAndPasswords, digester = digestFunction).authenticate(credentials)
            }
        }
        basic("basic-auth-TIME/RESET") {
            validate { credentials ->
                val usersAndPasswords: MutableMap<String, ByteArray> = check(allPrivileges.getPrivilege(Endpoints.TimeReset))
                UserHashedTableAuth(table = usersAndPasswords, digester = digestFunction).authenticate(credentials)
            }
        }
        basic("basic-auth-TIME/INCREMENT") {
            validate { credentials ->
                val usersAndPasswords: MutableMap<String, ByteArray> = check(allPrivileges.getPrivilege(Endpoints.TimeIncrement))
                UserHashedTableAuth(table = usersAndPasswords, digester = digestFunction).authenticate(credentials)
            }
        }
        basic("basic-auth-TIME/DECREMENT") {
            validate { credentials ->
                val usersAndPasswords: MutableMap<String, ByteArray> = check(allPrivileges.getPrivilege(Endpoints.TimeDeterment))
                UserHashedTableAuth(table = usersAndPasswords, digester = digestFunction).authenticate(credentials)
            }
        }
    }
}


