package cute.nahida.hytbot.server.api.api.wnf

import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.server.APIHandler
import cute.nahida.hytbot.server.Handle
import cute.nahida.hytbot.server.Response

class WNFCloseProxy : APIHandler {
    override fun handle(handle: Handle): Response {
        val response = Response()

        response.code = 200
        response.msg = "执行成功"
        response.data.addProperty("status", HytBot.wnf.stopAllProxys())
        return response
    }
}
