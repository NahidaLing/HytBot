package cute.nahida.hytbot.server.api.api.script

import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.server.APIHandler
import cute.nahida.hytbot.server.Handle
import cute.nahida.hytbot.server.Response
import cute.nahida.hytbot.server.utils.throwables.ParamInvalidException
import cute.nahida.hytbot.server.utils.throwables.ParamNotFoundException

class RemoveScriptAll : APIHandler {
    override fun handle(handle: Handle): Response {
        val response = Response()

        var countSuccess = 0
        var countFailed = 0
        var countIgnored = 0

        HytBot.botsManager.bots.forEach {
            try {
                it.value.script.bindScript?.let { _ ->
                    if (it.value.script.isEnable) it.value.script.disable()
                    it.value.script.bindScript = null
                    countSuccess++
                } ?: {
                    countIgnored++
                }
                it.value.script.isEnable = false
            } catch (_: Throwable) {
                countFailed++
            }
        }

        response.code = 200
        response.msg = "设置成功"

        response.data.addProperty("success", countSuccess)
        response.data.addProperty("failed", countFailed)
        response.data.addProperty("ignored", countIgnored)

        return response
    }
}
