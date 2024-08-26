package cute.nahida.hytbot.server.api.api.wnf

import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.server.APIHandler
import cute.nahida.hytbot.server.Handle
import cute.nahida.hytbot.server.Response
import cute.nahida.hytbot.server.utils.throwables.ParamNotFoundException
import java.time.Instant

class WNFLogin : APIHandler {
    override fun handle(handle: Handle): Response {
        val response = Response()

        val user = handle.requestParams["user"] ?: run { throw ParamNotFoundException("user") }
        val pwd = handle.requestParams["pwd"] ?: run { throw ParamNotFoundException("pwd") }

        HytBot.wnf.login(user, pwd).also {
            if (!it) {
                response.code = 400
                response.msg = "登录失败"
                return response
            }
        }

        val javaSubscribe = HytBot.wnf.getUserData()?.javaSubscribe ?: 0L
        val currentTimestamp = Instant.now().epochSecond

        response.code = 200
        response.msg = "登录成功"
        response.data.addProperty("isActive", javaSubscribe > currentTimestamp)
        return response
    }
}
