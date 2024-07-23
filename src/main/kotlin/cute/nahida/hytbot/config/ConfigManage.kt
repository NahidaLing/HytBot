package cute.nahida.hytbot.config

import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.config.configs.Config
import cute.nahida.hytbot.utils.ymal.YamlConfig
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Paths
import kotlin.io.path.readText

class ConfigManage {
    private var configFile: String = "Config.yml"
    lateinit var configs: Config

    fun readConfig() {
        try {
            val configString = Paths.get(configFile).readText(StandardCharsets.UTF_8)
            configs = YamlConfig.loadFromString(configString, Config::class.java)
            HytBot.logger.info("加载配置成功")
        } catch (e: Exception) {
            HytBot.logger.warn("加载配置失败", e)
            defaultConfig()
        }
    }

    fun saveConfig() {
        try {
            Files.write(Paths.get(configFile), YamlConfig.saveToString(configs).toByteArray(StandardCharsets.UTF_8))
            HytBot.logger.info("保存配置成功")
        } catch (e: Exception) {
            HytBot.logger.warn("保存配置失败", e)
        }
    }

    fun defaultConfig(forceSave: Boolean = false) {
        if (forceSave || !Files.exists(Paths.get(configFile))) {
            configs = Config()
            saveConfig()
        }
    }

}
