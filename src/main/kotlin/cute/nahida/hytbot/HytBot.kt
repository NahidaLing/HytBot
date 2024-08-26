package cute.nahida.hytbot

import cute.nahida.hytbot.config.ConfigManage
import cute.nahida.hytbot.handle.bots.BotsManager
import cute.nahida.hytbot.handle.script.ScriptManager
import cute.nahida.hytbot.handle.update.UpdateManager
import cute.nahida.hytbot.server.WebServerManager
import cute.nahida.hytbot.utils.wnf.WNF
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Suppress("MemberVisibilityCanBePrivate")
object HytBot {
    lateinit var args: Array<String>
    val logger: Logger = LogManager.getLogger(HytBot::class.java)

    val configManager = ConfigManage()
    val botsManager = BotsManager()
    val webServerManager = WebServerManager()
    var updateManager = UpdateManager()
    var scriptManager = ScriptManager()

    var wnf = WNF("127.0.0.1:11710")

    fun start(args: Array<String>) {
        this.args = args

        Runtime.getRuntime().addShutdownHook(Thread{ stop() })

        configManager.readConfig()
        webServerManager.start()

        updateManager.reload()
        updateManager.addUpdate { botsManager.update() }

        scriptManager.loadScripts()
    }

    fun stop() {
        updateManager.stop()
        webServerManager.stop()
        botsManager.disconnectAll()

        logger.info("清理工作完成 程序退出")
    }
}