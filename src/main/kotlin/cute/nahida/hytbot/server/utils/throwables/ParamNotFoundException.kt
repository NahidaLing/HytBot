package cute.nahida.hytbot.server.utils.throwables


class ParamNotFoundException (
    val param: String
): WebServerHandleException()