package cute.nahida.hytbot.server.api.api

import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.server.APIHandler
import cute.nahida.hytbot.server.Handle
import cute.nahida.hytbot.server.Response
import cute.nahida.hytbot.server.utils.throwables.ParamInvalidException
import cute.nahida.hytbot.server.utils.throwables.ParamNotFoundException

class Login : APIHandler {
    override fun handle(handle: Handle): Response {
        val response = Response()

        val port = handle.requestParams["port"]?.toInt() ?: run { throw ParamNotFoundException("port") }
        if (port !in 1..65535) run { throw ParamInvalidException("port") }
        val count = handle.requestParams["count"]?.toInt() ?: 1
        if (count !in 1..100) run { throw ParamInvalidException("count") }

        HytBot.botsManager.login(port, count)

        response.code = 200
        response.msg = "登录任务已提交"
        response.data.addProperty("port", port)
        response.data.addProperty("count", count)

        return response
    }
}
