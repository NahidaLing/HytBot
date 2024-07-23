package cute.nahida.hytbot.server

interface APIHandler {
    fun handle(handle: Handle): Response
}