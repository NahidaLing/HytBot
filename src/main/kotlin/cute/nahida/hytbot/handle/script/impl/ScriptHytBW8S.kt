package cute.nahida.hytbot.handle.script.impl

import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.handle.bots.bot.Bot
import cute.nahida.hytbot.handle.bots.bot.BotPosition
import cute.nahida.hytbot.handle.script.Script
import cute.nahida.hytbot.handle.script.ScriptInfo
import cute.nahida.hytbot.handle.script.utils.misc.StaticCommands
import cute.nahida.hytbot.handle.script.utils.misc.Status

open class ScriptHytBW8S(name: String = "BW8S"): Script(name, ScriptInfo(ScriptInfo.SuperAccountMode.SINGLE)) {
    @Suppress("SpellCheckingInspection")
    open val commands = arrayOf("/germclick c3ViamVjdF9iZWR3YXI=", "/germsubclick IMKnZcKnbDjpmJ/ljZXkurrnu53mnYDmqKHlvI8=")

    companion object {
        val IN_GAME_POS: MutableList<BotPosition> = mutableListOf(
            // 默认图
            BotPosition( 610.5, 126.0, 467.5, 0.0f, 0.0f),
            // 无火 16 特殊
            BotPosition( 665.5, 111.5, 90.5, -180.0f, 0.0f),
            // 无火 32 特殊
            BotPosition(-24.699999988079067, 71.0, 435.69999998807907, -130.94986f, 14.850006f),
            BotPosition(7.5, 112.5, -267.5, 0f, 0f)
        )
    }

    private var coolDownJoin = 0
    private var coolDownLeave = 0

    override fun onStop() {
        leave(true)
    }

    override fun onUpdate() {
        if (coolDownJoin > 0) coolDownJoin--
        if (coolDownLeave > 0) coolDownLeave--

        join()
        leave()
    }

    override fun onMessage(msg: String) {
        when (msg) {
            //"起床战争>> 游戏开始 ..." -> bot.script.status = Status.ROOM_STARTED
        }
    }

    override fun onTeleport(position: BotPosition) {
        when (position) {
            IN_GAME_POS -> bot.script.status = Status.ROOM_WAIT_START
        }
    }

    private fun join() {
        if (bot.script.status == Status.HUB && coolDownJoin <= 0) {
            coolDownJoin = 20
            // 尝试打开游戏菜单
            bot.slot = 0
            bot.tryUseItem()
            Thread.sleep(100)
            // GermMod 执行 (WNF Only)
            commands.forEach { bot.sendMessage(it) }
        }
    }

    private fun leave(force: Boolean = false) {
        if ((bot.script.status == Status.ROOM_STARTED && coolDownLeave <= 0) || force) {
            if (bot.script.status == Status.ROOM_STARTED && !bot.script.superAccount) bot.sendMessage(HytBot.configManager.configs.message.on_game_started)
            coolDownLeave = 20
            bot.sendMessage(StaticCommands.COMMAND_HUB)
        }
    }
}
