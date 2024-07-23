package cute.nahida.hytbot.server.api

import cute.nahida.hytbot.server.APIHandler
import cute.nahida.hytbot.server.Handle
import cute.nahida.hytbot.server.Response
import java.time.Instant

class Index : APIHandler {
    override fun handle(handle: Handle): Response {
        val response = Response()

        response.code = 200
        response.msg = "200 OK 喵~"
        response.data.addProperty("now", Instant.now().toEpochMilli() / 1000)

        return response
    }
}
