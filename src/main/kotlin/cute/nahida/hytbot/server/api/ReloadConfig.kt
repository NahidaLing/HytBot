package cute.nahida.hytbot.server.api

import com.google.gson.Gson
import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.server.APIHandler
import cute.nahida.hytbot.server.Handle
import cute.nahida.hytbot.server.Response

class ReloadConfig : APIHandler {
    override fun handle(handle: Handle): Response {
        val response = Response()

        HytBot.configManager.readConfig()

        response.code = 200
        response.msg = "重新加载配置成功"

        response.data.add("config", Gson().toJsonTree(HytBot.configManager.configs))

        return response
    }
}
