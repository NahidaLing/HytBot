package cute.nahida.hytbot.server.api.api.script

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.server.APIHandler
import cute.nahida.hytbot.server.Handle
import cute.nahida.hytbot.server.Response

class ListScripts : APIHandler {
    override fun handle(handle: Handle): Response {
        val response = Response()

        val names = JsonArray()

        HytBot.scriptManager.getScriptMap().forEach {
            val obj = JsonObject()
            obj.addProperty("name", it.key)
            obj.addProperty("package_name", it.value.packageName)
            obj.addProperty("simple_name", it.value.simpleName)
            obj.addProperty("hash", it.value.hashCode())
            names.add(obj)
        }

        response.code = 200
        response.msg = "获取列表成功"

        response.data.add("scripts", names)
        response.data.addProperty("size", names.size())

        return response
    }
}
