package cute.nahida.hytbot.server.api.api.script

import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.server.APIHandler
import cute.nahida.hytbot.server.Handle
import cute.nahida.hytbot.server.Response
import cute.nahida.hytbot.server.utils.throwables.ParamInvalidException
import cute.nahida.hytbot.server.utils.throwables.ParamNotFoundException

class DisableScriptAll : APIHandler {
    override fun handle(handle: Handle): Response {
        val response = Response()

        var countSuccess = 0
        var countFailed = 0
        var countIgnored = 0

        HytBot.botsManager.bots.forEach {
            try {
                if (!it.value.script.isEnable) {
                    countIgnored++
                    return@forEach
                }
                it.value.script.disable()
                countSuccess++
            } catch (e: Throwable) {
                countFailed++
            }
        } 

        response.code = 200
        response.msg = "启动成功"

        response.data.addProperty("success", countSuccess)
        response.data.addProperty("failed", countFailed)
        response.data.addProperty("ignored", countIgnored)

        return response
    }
}
