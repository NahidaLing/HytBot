package cute.nahida.hytbot.server.utils.throwables


class ParamInvalidException (
    val param: String
): WebServerHandleException()