
package cute.nahida.hytbot.utils

import cute.nahida.hytbot.HytBot
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object HttpUtils {
    fun get(url: String, headers: Map<String, String> = emptyMap()): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        headers.forEach { (key, value) ->
            connection.setRequestProperty(key, value)
        }
        connection.connectTimeout = 5000
        connection.readTimeout = 5000

        val response = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }

        // HytBot.logger.info(response)
        return response
    }
    fun post(url: String, body: String, headers: Map<String, String> = emptyMap()): String {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/json")
        headers.forEach { (key, value) ->
            connection.setRequestProperty(key, value)
        }
        connection.connectTimeout = 5000
        connection.readTimeout = 5000

        connection.outputStream.use { outputStream ->
            OutputStreamWriter(outputStream, Charsets.UTF_8).use { writer ->
                writer.write(body)
            }
        }
        val response = connection.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }

        // HytBot.logger.info(response)
        return response
    }
}