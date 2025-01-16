package cute.nahida.hytbot.handle.bots.bot

import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.handle.script.Script
import cute.nahida.hytbot.handle.script.utils.misc.Status

@Suppress("MemberVisibilityCanBePrivate")
open class BotBindScript (
    /**
     * 上层对象
     */
    @JvmField
    val bot: Bot
) {
    /**
     * 超级用户
     * 通常是刷分时的大号
     * 只可以设置一个 虽然设置多个程序也可能跑..
     */
    var superAccount: Boolean = false
    /**
     * 机器人状态
     * 适用于游戏中 不是连接状态 比如说在大厅 在游戏里 之类的..
     */
    var status: Status = Status.HUB
        set (value) {
            if (field == value) return

            HytBot.logger.debug("[${bot.id}] 状态更新: ${value.name}")

            when (value) {
                Status.ROOM_WAIT_START -> bot.sendMessage(HytBot.configManager.configs.message.on_join_game)
                Status.ROOM_STARTED -> bot.sendMessage(HytBot.configManager.configs.message.on_game_started)

                else -> { }
            }

            field = value
        }
    /**
     * 是否已启动
     */
    var isEnable: Boolean = false
    /**
     * 插件实例
     */
    var bindScript: Script? = null

    fun enable(): Boolean {
        isEnable = bindScript?.onStart(bot) ?: false
        return isEnable
    }

    fun disable() {
        isEnable = false
        bindScript?.onStop()
        bot.script.status = Status.HUB
    }
}