package cute.nahida.hytbot

import cute.nahida.hytbot.config.ConfigManage
import cute.nahida.hytbot.handle.bots.BotsManager
import cute.nahida.hytbot.handle.script.ScriptManager
import cute.nahida.hytbot.handle.update.UpdateManager
import cute.nahida.hytbot.server.WebServerManager
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger

@Suppress("MemberVisibilityCanBePrivate")
object HytBot {
    val logger: Logger = LogManager.getLogger(HytBot::class.java)

    val configManager = ConfigManage()
    val botsManager = BotsManager()
    val webServerManager = WebServerManager()
    var updateManager = UpdateManager()
    var scriptManager = ScriptManager()

    fun start() {
        Runtime.getRuntime().addShutdownHook(Thread{ stop() })

        configManager.readConfig()
        webServerManager.start()

        updateManager.reload()

        scriptManager.loadScripts()
    }

    fun stop() {
        updateManager.stop()
        webServerManager.stop()
        botsManager.disconnectAll()

        logger.info("清理工作完成 程序退出")
    }
}