package cute.nahida.hytbot.server.api.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.server.APIHandler
import cute.nahida.hytbot.server.Handle
import cute.nahida.hytbot.server.Response

class ListBots : APIHandler {
    override fun handle(handle: Handle): Response {
        val response = Response()

        val bots = JsonArray()
        HytBot.botsManager.bots.onEach { (id, data) ->
            val obj = JsonObject()
            obj.addProperty("id", id)
            obj.addProperty("invalid", data.invalid)
            obj.addProperty("hash", data.hashCode())

            val dataConnect = JsonObject()
            dataConnect.addProperty("host", data.host)
            dataConnect.addProperty("port", data.port)
            dataConnect.addProperty("connected", data.isConnected())
            obj.add("connect", dataConnect)

            val dataProfiler = JsonObject()
            dataProfiler.addProperty("name", data.player.profiler.name)
            dataProfiler.addProperty("id", data.player.profiler.id.toString())
            dataProfiler.addProperty("is_complete", data.player.profiler.isComplete)
            obj.add("profiler", dataProfiler)

            val dataScript = JsonObject()
            dataScript.addProperty("super_account", data.script.superAccount)
            dataScript.addProperty("script_status", data.script.isEnable)
            dataScript.addProperty("script_name", data.script.bindScript?.name)
            dataScript.addProperty("in_game_status_code", data.script.status.code)
            dataScript.addProperty("in_game_status_name", data.script.status.name)
            obj.add("script", dataScript)

            val dataIRC = JsonObject()
            dataIRC.addProperty("enable", data.irc != null)
            dataIRC.addProperty("online", data.irc?.isLogin ?: false)
            dataIRC.addProperty("session", data.irc?.sessionManager?.selfSession?.uniqueId?.toString())
            obj.add("irc", dataIRC)

            bots.add(obj)
        }

        response.code = 200
        response.msg = "获取成功"

        response.data.add("bots", bots)
        response.data.addProperty("size", bots.size())

        return response
    }
}
