package cute.nahida.hytbot.handle.bots

import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.handle.bots.bot.Bot

class BotsManager {
    val bots: HashMap<String, Bot> = hashMapOf()


    /**
     * 登录机器入 需要配合脱盒使用
     *
     * @param startPort 起始端口
     * @param count 登录数量
     */
    fun login(startPort: Int, count: Int = 1) {
        for(i in startPort..<startPort + count) {
            val bot = Bot(i.toString())
            if (!check(i.toString())) bot.start(HytBot.configManager.configs.connect, i)
            bots[i.toString()] = bot
        }
    }
    /**
     * 检查某个机器人id是否存在
     *
     * @param id 机器人id
     */
    fun check(id: String) = bots.containsKey(id)
    /**
     * 断开连接
     *
     * @param id 就是 端口号.toString()
     */
    fun disconnect(id: String): Boolean {
        bots[id]?.disconnect() ?: return false
        bots.remove(id)
        return true
    }
    /**
     * 断开所有客户端连接
     */
    fun disconnectAll() {
        bots.forEach { it.value.disconnect() }
        bots.clear()
    }

    fun sendMessage(id: String, message: String): Boolean = bots[id]?.sendMessage(message) ?: false
    fun sendMessage(message: String) {
        bots.forEach {  it.value.sendMessage(message) }
    }
}
