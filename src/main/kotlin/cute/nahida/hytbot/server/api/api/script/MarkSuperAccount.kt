package cute.nahida.hytbot.server.api.api.script

import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.server.APIHandler
import cute.nahida.hytbot.server.Handle
import cute.nahida.hytbot.server.Response
import cute.nahida.hytbot.server.utils.throwables.ParamInvalidException
import cute.nahida.hytbot.server.utils.throwables.ParamNotFoundException

class MarkSuperAccount : APIHandler {
    override fun handle(handle: Handle): Response {
        val response = Response()

        val id = handle.requestParams["id"] ?: run { throw ParamNotFoundException("id") }
        var mark = handle.requestParams["mark"]


        HytBot.botsManager.bots[id]?.let { bot ->
            mark?.let {
                bot.script.superAccount = it == "true"
                response.msg = "设置成功"
            } ?: run {
                mark = if (bot.script.superAccount) "true" else "false"
                response.msg = "获取成功"
            }

        } ?: run { throw ParamInvalidException("id") }

        response.code = 200

        response.data.addProperty("id", id)
        response.data.addProperty("mark", mark == "true")

        return response
    }
}
