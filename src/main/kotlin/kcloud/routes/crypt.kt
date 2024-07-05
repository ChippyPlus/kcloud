package kcloud.routesimport io.ktor.http.*import io.ktor.server.application.*import io.ktor.server.auth.*import io.ktor.server.request.*import io.ktor.server.response.*import io.ktor.server.routing.*import kcloud.logimport javax.crypto.BadPaddingExceptionimport javax.crypto.Cipherimport javax.crypto.KeyGeneratorimport javax.crypto.SecretKeyimport javax.crypto.spec.IvParameterSpecimport javax.crypto.spec.SecretKeySpecfun stringArraIntoByteArra(string: String): ByteArray {    var e = byteArrayOf()    for (i in string.split(",")) {        e += byteArrayOf(i.toByte())    }    return e}fun aesEncrypt(data: ByteArray, secretKey: SecretKey, encrypt: Boolean = true): ByteArray {    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")    val ivParameterSpec = IvParameterSpec(ByteArray(16)) // Use a secure IV in production    if (encrypt) {        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)    } else {        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)    }    return cipher.doFinal(data)}fun Application.configureCryptRouting() {    routing {        authenticate("basic-auth") {            get("/crypt/generatekey/{algo}") {                if (call.parameters["algo"] == "aes") {                    val keyGenerator = KeyGenerator.getInstance("AES")                    keyGenerator.init(256)                    val secretKey = keyGenerator.generateKey().encoded.joinToString(",")                    call.respond(secretKey)                    log(                        "crypt", "crypt/generatekey",                        "generated a new key using " + "\"${call.parameters["algo".uppercase()]}\""                    )                    log(whereDidItHappen = "crypt/generatekey")                } else {                    call.respondText(text = "algorithmUnknown", status = HttpStatusCode.NotFound)                }            }            get("/crypt/encrypt/{algo}") {                if (call.parameters["algo"] == "aes") {                    val body = call.receive<Map<String, String>>()                    val newKey = stringArraIntoByteArra(body["key"]!!)                    val secretKey: SecretKey = SecretKeySpec(newKey, "AES")                    val crypt = aesEncrypt(body["message"]!!.toByteArray(), secretKey, true)                    call.respond(crypt.joinToString(","))                    log(                        "crypt", "crypt/encrypt", "encrypted using " + "\"${call.parameters["algo".uppercase()]}\""                    )                    log(whereDidItHappen = "crypt/encrypt")                } else {                    call.respondText(text = "algorithmUnknown", status = HttpStatusCode.NotFound)                }            }            get("/crypt/decrypt/{algo}") {                if (call.parameters["algo"] == "aes") {                    val body = call.receive<Map<String, String>>()                    val newKey = stringArraIntoByteArra(body["key"]!!)                    val secretKey: SecretKey = SecretKeySpec(newKey, "AES")                    try {                        val crypt = aesEncrypt(stringArraIntoByteArra(body["message"]!!), secretKey, false)                        call.respond(String(crypt))                        log(                            "crypt", "crypt/encrypt", "decrypted using " + "\"${call.parameters["algo".uppercase()]}\""                        )                        log(whereDidItHappen = "crypt/decrypt")                    } catch (badKeyOrMessageException: BadPaddingException) {                        call.respondText(text = "BadPadding", status = HttpStatusCode.BadRequest)                    }                } else {                    call.respondText(text = "algorithmUnknown", status = HttpStatusCode.NotFound)                }            }        }    }}