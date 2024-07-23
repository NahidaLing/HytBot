package cute.nahida.hytbot.server


class Handle (
    var requestPath: String,
    var requestParams: Map<String, String> = hashMapOf()
)
