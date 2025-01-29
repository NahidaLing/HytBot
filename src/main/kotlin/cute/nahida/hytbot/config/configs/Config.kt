package cute.nahida.hytbot.config.configs

@Suppress("Unused", "PropertyName") // 配置不需要遵循命名规定
class Config {
    @JvmField
    var server = Server()

    class Server {
        @JvmField
        var port: Int = 4330
    }

    @JvmField
    var bot = ConfigBot()

    class ConfigBot {
        /**
         * 多账号登录间隔
         * 过快登录服务器会导致 x19 账号掉线
         */
        @JvmField
        var loginDelay = 1000L
        /**
         * 关闭 Myth 的绕绿功能
         */
        @JvmField
        var hackerBrand = false
    }

    @JvmField
    var message: Message = Message()

    class Message {
        @JvmField
        var on_join_game = ""
        @JvmField
        var on_game_started = ""
    }

    @JvmField
    var connect: String = "127.0.0.1"
}
