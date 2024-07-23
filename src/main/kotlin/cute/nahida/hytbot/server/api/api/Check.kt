package cute.nahida.hytbot.server.api.api

import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.server.APIHandler
import cute.nahida.hytbot.server.Handle
import cute.nahida.hytbot.server.Response
import cute.nahida.hytbot.server.utils.throwables.ParamNotFoundException

class Check : APIHandler {
    override fun handle(handle: Handle): Response {
        val response = Response()

        val id = handle.requestParams["id"] ?: run { throw ParamNotFoundException("id") }

        response.code = 200
        response.msg = "执行成功"
        response.data.addProperty("status", HytBot.botsManager.check(id))

        return response
    }
}
