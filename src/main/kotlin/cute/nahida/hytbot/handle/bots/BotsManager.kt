package cute.nahida.hytbot.handle.bots

import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.handle.bots.bot.Bot
import java.util.concurrent.Executors
import java.util.concurrent.Future

class BotsManager(@JvmField val base: HytBot) {
    val bots = linkedMapOf<String, Bot>()


    /**
     * 登录机器入 需要配合脱盒使用
     *
     * @param startPort 起始端口
     * @param count 登录数量
     */
    fun login(startPort: Int, count: Int = 1, ip: String = HytBot.configManager.configs.connect) {
        for(i in startPort..<startPort + count) {
            val bot = Bot(this, i.toString())
            if (!check(i.toString())) bot.start(ip, i)
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

    /**
     * 添加重新连接任务
     *
     * @param id 机器人id
     * @param force 是否强制重新连接(忽略机器人已连接)
     */
    fun addReconnectTask(id: String, force: Boolean = false): Boolean {
        if (bots[id]?.isConnected() == true && !force) return false
        bots[id]?.needReconnect = true
        return true
    }
    /**
     * 为所有机器人添加重新连接任务
     *
     * @param force 是否强制重新连接(忽略机器人已连接)
     */
    fun addReconnectTask(force: Boolean = false) {
        bots.forEach {  addReconnectTask(it.key, force) }
    }
    /**
     * 为所有机器人执行一遍更新
     * 执行可以刷新一次机器人状态 例如让机器人执行重新连接
     * 但是 通常你不需要执行此代码 且这会影响 Script 里面 update() 的频率
     *
     * @return 固定为 true  用于接入updateManage
     */
    fun update(): Boolean {
        // kotlin await 不能用 我裂开了
        val executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
        val futures: List<Future<*>> = bots.values.map { bot ->
            executor.submit {
                bot.update()
            }
        }

        futures.forEach { it.get() }

        executor.shutdown()
        return true
    }
}
