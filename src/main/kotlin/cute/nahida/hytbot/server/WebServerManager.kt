package cute.nahida.hytbot.server

import com.google.gson.JsonObject
import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpHandler
import com.sun.net.httpserver.HttpServer
import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.server.api.Index
import cute.nahida.hytbot.server.api.Ping
import cute.nahida.hytbot.server.api.api.*
import cute.nahida.hytbot.server.api.api.script.*
import cute.nahida.hytbot.server.utils.RequestParamsUtils
import cute.nahida.hytbot.server.utils.throwables.ParamInvalidException
import cute.nahida.hytbot.server.utils.throwables.ParamNotFoundException
import cute.nahida.hytbot.server.utils.throwables.WebServerHandleException
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.IOException
import java.net.InetSocketAddress
import java.nio.charset.StandardCharsets
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ThreadPoolExecutor

class WebServerManager {
    var server: HttpServer? = null

    private var apis: HashMap<String, APIHandler> = HashMap()

    private var threadPoolExecutor: ThreadPoolExecutor? = null


    @Throws(Exception::class)
    fun start() {
        try {
            threadPoolExecutor = Executors.newFixedThreadPool(20) as ThreadPoolExecutor
            HytBot.logger.info("[WebServer] 初始化线程池成功")
        } catch (e: Exception) {
            HytBot.logger.error("[WebServer] 初始化线程池失败", e)
            throw e
        }

        try {
            val server = HttpServer.create(InetSocketAddress(HytBot.configManager.configs.server.port), 0)
            server.createContext("/", HttpHandle(this))
            server.executor = threadPoolExecutor
            server.start()
            HytBot.logger.info("[WebServer] 启动成功 监听于 0.0.0.0:${HytBot.configManager.configs.server.port}")
        } catch (e: Exception) {
            HytBot.logger.error("[WebServer] 绑定 0.0.0.0:${HytBot.configManager.configs.server.port} 失败", e)
        }

        addEndPoint("/", Index())
        addEndPoint("/Ping", Ping())

        addEndPoint("/api/Opt", Opt())
        addEndPoint("/api/Login", Login())
        addEndPoint("/api/Check", Check())
        addEndPoint("/api/Offline", Offline())
        addEndPoint("/api/OfflineAll", OfflineAll())
        addEndPoint("/api/ListBots", ListBots())
        addEndPoint("/api/SendMessage", SendMessage())
        addEndPoint("/api/SendMessageAll", SendMessageAll())

        addEndPoint("/api/script/ListScripts", ListScripts())
        addEndPoint("/api/script/SetScript", SetScript())
        addEndPoint("/api/script/SetScriptAll", SetScriptAll())
        addEndPoint("/api/script/RemoveScript", RemoveScript())
        addEndPoint("/api/script/RemoveScriptAll", RemoveScriptAll())
        addEndPoint("/api/script/MarkSuperAccount", RemoveScript())
        addEndPoint("/api/script/EnableScript", EnableScript())
        addEndPoint("/api/script/EnableScriptAll", EnableScriptAll())
        addEndPoint("/api/script/DisableScript", DisableScript())
        addEndPoint("/api/script/DisableScriptAll", DisableScriptAll())

    }

    fun stop() {
        server?.stop(0)
        HytBot.logger.info("[WebServer] 停止服务成功")
        threadPoolExecutor?.shutdown()
        HytBot.logger.info("[WebServer] 关闭线程池成功")
    }

    private fun addEndPoint(s: String, api: APIHandler) {
        apis[s.lowercase(Locale.getDefault())] = api
    }


    internal class HttpHandle(private val manager: WebServerManager) : HttpHandler {
        @Throws(IOException::class)
        override fun handle(t: HttpExchange) {
            try {
                HytBot.logger.info("[WebServer] ${t.remoteAddress.hostString}:${t.remoteAddress.port}  ${t.requestMethod} ${t.requestURI.path}")
                t.responseHeaders["Access-Control-Allow-Origin"] = "*"
                when (t.requestMethod) {
                    "OPTIONS" -> {
                        t.responseHeaders.add("Access-Control-Allow-Methods", "GET, OPTIONS, POST")
                        t.responseHeaders.add("Access-Control-Allow-Headers", "*")
                        t.sendResponseHeaders(200, 0)
                        val os = t.responseBody
                        os.write(0)
                        os.close()
                        t.close()
                        return
                    }
                    "GET" -> {
                        val handle = Handle(t.requestURI.path.lowercase(Locale.getDefault()), RequestParamsUtils.getParams(t))
                        var response = Response()

                        manager.apis[handle.requestPath]?.let {
                            try {
                                response = it.handle(handle)
                            } catch (e: WebServerHandleException) {
                                when(e) {
                                    is ParamNotFoundException -> {
                                        response.code = 400
                                        response.msg = "参数 ${e.param} 不存在"
                                    }
                                    is ParamInvalidException -> {
                                        response.code = 400
                                        response.msg = "参数 ${e.param} 无效"
                                    }
                                }
                            }
                        } ?: run {
                            response.code = 404
                            response.msg = "404 Not Found."
                            response.data.addProperty("node", handle.requestPath)
                        }

                        t.responseHeaders["Content-Type"] = response.headerContentType

                        val json = JsonObject()
                        json.addProperty("code", response.code)
                        json.addProperty("msg", response.msg)
                        json.add("data", response.data)

                        val responseByte = json.toString().toByteArray(StandardCharsets.UTF_8)

                        t.sendResponseHeaders(response.code, responseByte.size.toLong())

                        val os = t.responseBody
                        os.write(responseByte)
                        os.close()

                        t.close()
                    }
                }


        } catch (e: Exception) {
            logger.error("[WebServer] 处理请求时发生异常", e)
            throw e
        }
    }



    companion object {
        private val logger: Logger = LogManager.getLogger(
            WebServerManager::class.java
        )
    }
}
}
