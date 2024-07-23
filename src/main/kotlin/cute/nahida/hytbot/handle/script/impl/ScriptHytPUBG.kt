package cute.nahida.hytbot.handle.script.impl

import cute.nahida.hytbot.handle.bots.bot.Bot
import cute.nahida.hytbot.handle.bots.bot.BotPosition
import cute.nahida.hytbot.handle.script.Script
import cute.nahida.hytbot.handle.script.utils.misc.StaticCommands
import cute.nahida.hytbot.handle.script.utils.misc.Status

class ScriptHytPUBG: Script("PUBG") {
    companion object {
        private val IN_GAME_POS = BotPosition(1377.0, 227.0, 22.0, 260.84973f, 21.749983f)
    }
    private lateinit var bot: Bot

    private var coolDownJoin = 0
    private var coolDownLeave = 0

    override fun onStart(bot: Bot) {
        this.bot = bot
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

    override fun onTitle(title: String?, subTitle: String?) {
        when (title) {
            "花雨庭" -> bot.script.status = Status.HUB
            "§a飞行中..." -> bot.script.status = Status.ROOM_STARTED
        }
    }

    override fun onTeleport(position: BotPosition) {
        if (position == IN_GAME_POS) bot.script.status = Status.ROOM_WAIT_START
    }


    private fun join() {
        if (bot.script.status == Status.HUB && coolDownJoin <= 0) {
            coolDownJoin = 5
            // 尝试打开游戏菜单
            bot.slot = 0
            bot.tryUseItem()
            Thread.sleep(100)
            // GermMod 执行 (WNF Only)
            @Suppress("SpellCheckingInspection")
            bot.sendMessage("/germclick c3ViamVjdF9sZWlzdXJl")
            @Suppress("SpellCheckingInspection")
            bot.sendMessage("/germclick c3ViamVjdF9maWdodF90ZWFt")
            @Suppress("SpellCheckingInspection")
            bot.sendMessage("/germsubclick IMKnZcKnbOWQg+m4oeWNleS6ug==")
        }
    }

    private fun leave(force: Boolean = false) {
        if ((bot.script.status == Status.ROOM_STARTED && coolDownLeave <= 0) || force) {
            coolDownLeave = 5
            bot.sendMessage(StaticCommands.COMMAND_HUB)
        }
    }
}
