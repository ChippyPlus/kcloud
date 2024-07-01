import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

fun byteArraIntoString(byteArray: ByteArray): String {
    return byteArray.joinToString(separator = ",") { it.toString() }
}

fun stringArraIntoByteArra(string: String): ByteArray {
    var e = byteArrayOf()
    for ((index, i) in string.split(",").withIndex()) {
        e += byteArrayOf(i.toByte())
    }
    return e
}


fun main() {
    var og = byteArrayOf()
    val originalText = "Hello World!"
    val old = generateAESKey(256).encoded
    for (i in old) {
        og += byteArrayOf(i)
    }
    /*
    (byteArraIntoString(og)
    */
    val newKey = stringArraIntoByteArra("-23,-87,-81,10,69,-20,66,-126,50,9,3,80,-66,-77,6,-57,-14,-103,-14,15,111,103,-97,16,-96,22,-36,120,100,-99,-115,114")
    val algorithm = "AES" // Replace with your desired algorithm
    val secretKey: SecretKey = SecretKeySpec(newKey, algorithm)


    val encryptedData = aesEncrypt(originalText.toByteArray(), secretKey)
    val decryptedData = aesDecrypt(encryptedData, secretKey)
    val decryptedText = String(decryptedData)
    println(decryptedText)
}


fun aesDecrypt(encryptedData: ByteArray, secretKey: SecretKey): ByteArray {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val ivParameterSpec = IvParameterSpec(ByteArray(16)) // Use the same IV as used in encryption
    cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec)
    return cipher.doFinal(encryptedData)
}

fun aesEncrypt(data: ByteArray, secretKey: SecretKey): ByteArray {
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val ivParameterSpec = IvParameterSpec(ByteArray(16)) // Use a secure IV in production
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
    return cipher.doFinal(data)
}

fun generateAESKey(keySize: Int = 256): SecretKey {
    val keyGenerator = KeyGenerator.getInstance("AES")
    keyGenerator.init(keySize)
    return keyGenerator.generateKey()
}