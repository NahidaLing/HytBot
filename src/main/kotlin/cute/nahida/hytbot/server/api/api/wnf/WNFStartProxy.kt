package cute.nahida.hytbot.server.api.api.wnf

import com.google.gson.JsonArray
import com.google.gson.JsonPrimitive
import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.server.APIHandler
import cute.nahida.hytbot.server.Handle
import cute.nahida.hytbot.server.Response
import cute.nahida.hytbot.server.utils.throwables.ParamNotFoundException

class WNFStartProxy : APIHandler {
    override fun handle(handle: Handle): Response {
        val response = Response()

        val accounts = handle.requestParams["account"]?.split(",") ?: run { throw ParamNotFoundException("account") }

        val loginPorts: MutableList<Int> = mutableListOf()

        HytBot.wnf.getGameAccounts()?.forEach {
            if (accounts.contains(it.user_id.toString())) {
                HytBot.wnf.loginGameAccount(it)
                HytBot.wnf.getRoleNames("77114517833647104")?.forEach runProxy@{ role ->
                    val proxy = HytBot.wnf.startProxy(role) ?: return@runProxy
                    HytBot.logger.info("[WNFUtils] 账号 ${it.user_id}/${role.roleName} 代理启动成功  ${proxy.serverAddr} -> ${proxy.localProxy}")
                    HytBot.botsManager.login(proxy.localProxy.port, 1, proxy.localProxy.ip)
                    loginPorts.add(proxy.localProxy.port)
                } ?: HytBot.logger.info("[WNFUtils] 账号 ${it.user_id} 游戏角色列表获取异常")
            }
        } ?: run {
            response.code = 400
            response.msg = "批量登录处理失败"
            return response
        }

        response.code = 200
        response.msg = "批量登录处理成功"
        val portsJsonArray = JsonArray()
        loginPorts.forEach { port ->
            portsJsonArray.add(JsonPrimitive(port))
        }
        response.data.add("ports", portsJsonArray)
        return response
    }
}
