package cute.nahida.hytbot.config.configs

@Suppress("Unused", "PropertyName", "SpellCheckingInspection") // 配置不需要遵循命名规定
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

    var irc = IRC()

    class IRC {
        /**
         * 是否启用 IRC 登录
         * 启动后 机器入 会连接相关服务器并上报自身游戏 ID
         *
         * 仅对此IRC做适配 `https://github.com/DarkMeowTeam/DarkIRC/`
         */
        @JvmField
        var enable = false
        /**
         * IRC 服务器
         */
        @JvmField
        var server = IRCServer()

        class IRCServer {
            /**
             * IRC 服务器域名/IP地址
             */
            @JvmField
            var host = "irc.nekocurit.asia"
            /**
             * IRC 服务器端口号
             */
            @JvmField
            var port = 48088
        }

        var login = IRCLogin()

        class IRCLogin {
            /**
             * 登录用户名
             */
            @JvmField
            var name = ""
            /**
             * 登录凭据
             * 请不要使用密码登录 因为他无法完成自动凭据转换
             */
            @JvmField
            var token = ""
            /**
             * 客户端标识
             */
            var brand = IRCClientBrand()

            class IRCClientBrand {
                /**
                 * 客户端标识 ID
                 * 其他人可见
                 */
                var id = "Bot"
                /**
                 * 客户端标识 验证 hash
                 */
                var hash = "123456"
                /**
                 * 客户端版本号 ID
                 */
                var version_id = 0
                /**
                 * 客户端版本号 名称
                 * 其他人可见
                 */
                var version_name = ""
            }
        }
    }
}
