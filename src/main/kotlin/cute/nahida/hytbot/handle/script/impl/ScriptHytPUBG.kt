package cute.nahida.hytbot.handle.script.impl

import cute.nahida.hytbot.handle.script.Script
import cute.nahida.hytbot.handle.script.ScriptInfo
import cute.nahida.hytbot.handle.script.manager.ScriptHytJoinGameData
import cute.nahida.hytbot.handle.script.utils.misc.Status

class ScriptHytPUBG: Script("PUBG", ScriptInfo(ScriptInfo.SuperAccountMode.SINGLE)) {

    init {
        joinGameManager.game = ScriptHytJoinGameData(4, "TEAM_FIGHT/pubg-solo")
    }

    override fun onTitle(title: String?, subTitle: String?) {
        super.onTitle(title, subTitle)

        when (title) {
            "§a飞行中..." -> bot.script.status = Status.ROOM_STARTED
        }
    }

    override fun onUpdateScoreboardTitle(title: String) {
        super.onUpdateScoreboardTitle(title)

        if (title == "§e§l代号:吃鸡" && bot.script.status == Status.HUB) bot.script.status = Status.ROOM_WAIT_START
    }
}
