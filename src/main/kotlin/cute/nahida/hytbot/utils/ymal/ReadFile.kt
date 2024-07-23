package cute.nahida.hytbot.utils.ymal

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets

object ReadFile {
    @JvmStatic
    @Throws(IOException::class)
    fun readFormFile(file: File): String {
        val fileLen = file.length()
        if (fileLen > 2147483647L) {
            throw IOException("文件太大无法读取")
        } else {
            val bytes = ByteArray(fileLen.toInt())
            val fileInputStream = FileInputStream(file)

            for (i in bytes.indices) {
                bytes[i] = fileInputStream.read().toByte()
            }

            fileInputStream.close()
            return String(bytes, StandardCharsets.UTF_8)
        }
    }

    @Throws(IOException::class)
    fun writeToFile(s: String, file: File) {
        val pa = file.parentFile
        pa?.mkdirs()

        file.createNewFile()
        val fileOutputStream = FileOutputStream(file)
        fileOutputStream.write(s.toByteArray(StandardCharsets.UTF_8))
        fileOutputStream.close()
    }
}
