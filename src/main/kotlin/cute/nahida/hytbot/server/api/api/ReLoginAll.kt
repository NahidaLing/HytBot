package cute.nahida.hytbot.server.api.api

import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.server.APIHandler
import cute.nahida.hytbot.server.Handle
import cute.nahida.hytbot.server.Response

class ReLoginAll : APIHandler {
    override fun handle(handle: Handle): Response {
        val response = Response()

        val force =  (handle.requestParams["force"] ?: "false") == "true"

        HytBot.botsManager.addReconnectTask(force)

        response.code = 200
        response.msg = "任务执行成功"

        return response
    }
}
