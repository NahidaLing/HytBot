package cute.nahida.hytbot.server.api.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.server.APIHandler
import cute.nahida.hytbot.server.Handle
import cute.nahida.hytbot.server.Response
import cute.nahida.hytbot.server.utils.throwables.ParamInvalidException
import cute.nahida.hytbot.server.utils.throwables.ParamNotFoundException

class ListBots : APIHandler {
    override fun handle(handle: Handle): Response {
        val response = Response()

        val bots = JsonArray()
        HytBot.botsManager.bots.forEach {
            val obj = JsonObject()
            obj.addProperty("id", it.key)
            obj.addProperty("invalid", it.value.invalid)
            obj.addProperty("hash", it.value.hashCode())

            val objConnect = JsonObject()
            objConnect.addProperty("host", it.value.host)
            objConnect.addProperty("port", it.value.port)
            objConnect.addProperty("connected", it.value.isConnected())
            obj.add("connect", objConnect)

            val objScript = JsonObject()
            objScript.addProperty("super_account", it.value.script.superAccount)
            objScript.addProperty("script_status", it.value.script.isEnable)
            objScript.addProperty("script_name", it.value.script.bindScript?.name)
            objScript.addProperty("in_game_status_code", it.value.script.status.code)
            objScript.addProperty("in_game_status_name", it.value.script.status.name)
            obj.add("script", objScript)

            bots.add(obj)
        }

        response.code = 200
        response.msg = "获取成功"

        response.data.add("bots", bots)
        response.data.addProperty("size", bots.size())

        return response
    }
}
