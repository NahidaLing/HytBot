package cute.nahida.hytbot.config.configs

@Suppress("Unused", "PropertyName") // 配置不需要遵循命名规定
class Config {
    var server: Server = Server()
    class Server {
        var port: Int = 4330
    }
    var message: Message = Message()
    class Message {
        var on_join_game = ""
        var on_game_started = ""
    }

    var connect: String = "127.0.0.1"
}
