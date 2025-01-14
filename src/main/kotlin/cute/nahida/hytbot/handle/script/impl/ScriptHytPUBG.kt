package cute.nahida.hytbot.handle.script.impl

import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.handle.bots.bot.BotPosition
import cute.nahida.hytbot.handle.script.Script
import cute.nahida.hytbot.handle.script.ScriptInfo
import cute.nahida.hytbot.handle.script.manager.ScriptHytJoinGameData
import cute.nahida.hytbot.handle.script.utils.misc.Status

class ScriptHytPUBG: Script("PUBG", ScriptInfo(ScriptInfo.SuperAccountMode.SINGLE)) {
    companion object {
        private val IN_GAME_POS = BotPosition(1377.0, 227.0, 22.0, 260.84973f, 21.749983f)
    }

    init {
        joinGameManager.game = ScriptHytJoinGameData(4, "TEAM_FIGHT/pubg-solo")
    }

    override fun onTitle(title: String?, subTitle: String?) {
        super.onTitle(title, subTitle)

        when (title) {
            "§a飞行中..." -> bot.script.status = Status.ROOM_STARTED
        }
    }

    override fun onTeleport(position: BotPosition) {
        if (position == IN_GAME_POS && bot.script.status == Status.HUB) {
            bot.script.status = Status.ROOM_WAIT_START
            if (!bot.script.superAccount) bot.sendMessage(HytBot.configManager.configs.message.on_join_game)
        }
    }
}
