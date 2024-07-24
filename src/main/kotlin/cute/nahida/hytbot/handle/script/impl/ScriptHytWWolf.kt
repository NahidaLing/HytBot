package cute.nahida.hytbot.handle.script.impl

import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.handle.bots.bot.Bot
import cute.nahida.hytbot.handle.script.Script
import cute.nahida.hytbot.handle.script.utils.misc.StaticCommands
import cute.nahida.hytbot.handle.script.utils.misc.Status

class ScriptHytWWolf: Script("WWolf") {
    private lateinit var bot: Bot

    private var coolDownJoin = 0
    private var coolDownLeave = 0

    override fun onStart(bot: Bot): Boolean {
        if (bot.script.superAccount) HytBot.logger.warn("[Script] 模式 WWolf 不支持 SuperAccount 设置 已忽略")
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
        when {
            msg.contains("[狼人杀Ⅱ]") && msg.contains("加入了游戏") -> bot.script.status = Status.ROOM_WAIT_START
            msg == "[狼人杀Ⅱ] 游戏开始！" -> bot.script.status = Status.ROOM_STARTED
        }
    }

    override fun onTitle(title: String?, subTitle: String?) {
        when (title) {
            "花雨庭" -> bot.script.status = Status.HUB
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
            @Suppress("SpellCheckingInspection")
            bot.sendMessage("/germclick c3ViamVjdF9sZWlzdXJl")
            @Suppress("SpellCheckingInspection")
            bot.sendMessage("/germsubclick IMKnZcKnbOeLvOS6uuadgA==")
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
