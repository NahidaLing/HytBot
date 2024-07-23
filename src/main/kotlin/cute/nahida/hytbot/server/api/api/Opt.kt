package cute.nahida.hytbot.server.api.api

import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.server.APIHandler
import cute.nahida.hytbot.server.Handle
import cute.nahida.hytbot.server.Response
import cute.nahida.hytbot.server.utils.throwables.ParamInvalidException
import cute.nahida.hytbot.server.utils.throwables.ParamNotFoundException

class Opt : APIHandler {
    override fun handle(handle: Handle): Response {
        val response = Response()

        val message = handle.requestParams["message"] ?: run { throw ParamNotFoundException("message") }

        HytBot.logger.info(message)

        response.code = 200
        response.msg = "发送控制台消息成功"
        response.data.addProperty("message", message)

        return response
    }
}
