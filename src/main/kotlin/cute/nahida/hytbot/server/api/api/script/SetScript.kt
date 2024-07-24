package cute.nahida.hytbot.server.api.api.script

import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.server.APIHandler
import cute.nahida.hytbot.server.Handle
import cute.nahida.hytbot.server.Response
import cute.nahida.hytbot.server.utils.throwables.ParamInvalidException
import cute.nahida.hytbot.server.utils.throwables.ParamNotFoundException

class SetScript : APIHandler {
    override fun handle(handle: Handle): Response {
        val response = Response()

        val id = handle.requestParams["id"] ?: run { throw ParamNotFoundException("id") }
        val script = handle.requestParams["script"] ?: run { throw ParamNotFoundException("script") }

        HytBot.scriptManager.get(script)?.let { scriptInstance ->
            HytBot.botsManager.bots[id]?.let {
                if (it.script.isEnable) it.script.disable()
                it.script.bindScript = scriptInstance
                it.script.isEnable = false
            } ?: run { throw ParamInvalidException("id") }
        } ?: run { throw ParamInvalidException("script") }

        response.code = 200
        response.msg = "设置成功"

        response.data.addProperty("id", id)

        return response
    }
}
