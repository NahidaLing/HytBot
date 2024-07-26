package cute.nahida.hytbot.handle.script.impl

import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.handle.bots.bot.Bot
import cute.nahida.hytbot.handle.bots.bot.BotPosition
import cute.nahida.hytbot.handle.script.Script
import cute.nahida.hytbot.handle.script.ScriptInfo
import cute.nahida.hytbot.handle.script.utils.misc.StaticCommands
import cute.nahida.hytbot.handle.script.utils.misc.Status

open class ScriptHytSWS(name: String = "SW-S"): Script(name, ScriptInfo(ScriptInfo.SuperAccountMode.SINGLE)) {
    @Suppress("SpellCheckingInspection")
    open val commands = arrayOf("/germclick c3ViamVjdF9za3l3YXI=", "/germsubclick IMKnZcKnbOepuuWym+aImOS6ieWNleS6ug==")


    private lateinit var bot: Bot

    private var coolDownJoin = 0
    private var coolDownLeave = 0

    override fun onStart(bot: Bot): Boolean {
        this.bot = bot
        return true
    }
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
            "已为你自动开启 伤害显示, 输入 /shoff 关闭" -> bot.script.status = Status.ROOM_WAIT_START
        }
    }

    override fun onTitle(title: String?, subTitle: String?) {
        when (title) {
            "花雨庭" -> bot.script.status = Status.HUB
        }
        when (subTitle) {
            "§bFighting" -> bot.script.status = Status.ROOM_STARTED
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
