package cute.nahida.hytbot.config.configs

@Suppress("Unused")
class Config {
    var server: Server = Server()
    class Server {
        var port: Int = 4330
    }

    var connect: String = "127.0.0.1"
}
