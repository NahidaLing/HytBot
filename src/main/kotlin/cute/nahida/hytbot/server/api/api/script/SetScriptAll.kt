package cute.nahida.hytbot.server.api.api.script

import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.server.APIHandler
import cute.nahida.hytbot.server.Handle
import cute.nahida.hytbot.server.Response
import cute.nahida.hytbot.server.utils.throwables.ParamInvalidException
import cute.nahida.hytbot.server.utils.throwables.ParamNotFoundException

class SetScriptAll : APIHandler {
    override fun handle(handle: Handle): Response {
        val response = Response()

        val script = handle.requestParams["script"] ?: run { throw ParamNotFoundException("script") }
        val force = handle.requestParams["force"] == "true"
        if (!HytBot.scriptManager.container(script)) {
            throw ParamInvalidException("script")
        }

        var countSuccess = 0
        var countFailed = 0
        var countIgnored = 0

        HytBot.botsManager.bots.forEach {
            try {
                HytBot.scriptManager.get(script)?.let { scriptInstance ->
                    if (force || it.value.script.bindScript?.name != script) {
                        if (it.value.script.isEnable) it.value.script.disable()
                        it.value.script.bindScript = scriptInstance
                        it.value.script.isEnable = false
                        countSuccess++
                    } else {
                        countIgnored++
                    }
                } ?: run {
                    countFailed++
                }
            } catch (e: Throwable) {
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
