package cute.nahida.hytbot.server.api.api

import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.server.APIHandler
import cute.nahida.hytbot.server.Handle
import cute.nahida.hytbot.server.Response
import cute.nahida.hytbot.server.utils.throwables.ParamNotFoundException

class OfflineAll : APIHandler {
    override fun handle(handle: Handle): Response {
        val response = Response()

        HytBot.botsManager.disconnectAll()

        response.code = 200
        response.msg = "任务执行成功"

        return response
    }
}
