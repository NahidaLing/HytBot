package cute.nahida.hytbot.server.api.api.script

import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.server.APIHandler
import cute.nahida.hytbot.server.Handle
import cute.nahida.hytbot.server.Response
import cute.nahida.hytbot.server.utils.throwables.ParamInvalidException
import cute.nahida.hytbot.server.utils.throwables.ParamNotFoundException

class RemoveScript : APIHandler {
    override fun handle(handle: Handle): Response {
        val response = Response()

        val id = handle.requestParams["id"] ?: run { throw ParamNotFoundException("id") }

        HytBot.botsManager.bots[id]?.let {
            it.script.bindScript = null
        } ?: run { throw ParamInvalidException("id") }

        response.code = 200
        response.msg = "设置成功"

        response.data.addProperty("id", id)

        return response
    }
}
