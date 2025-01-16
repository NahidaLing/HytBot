package cute.nahida.hytbot.handle.script.impl

import cute.nahida.hytbot.handle.script.Script
import cute.nahida.hytbot.handle.script.ScriptInfo
import cute.nahida.hytbot.handle.script.manager.ScriptHytJoinGameData
import cute.nahida.hytbot.handle.script.utils.misc.Status

class ScriptHytHP: Script("HP", ScriptInfo(ScriptInfo.SuperAccountMode.SINGLE)) {

    init {
        joinGameManager.game = ScriptHytJoinGameData(4, "LEISURE/hp-game")
    }

    override fun onMessage(msg: String) {
        super.onMessage(msg)

        if (msg.contains("准备时间已结束")) bot.script.status = Status.ROOM_STARTED
    }

    override fun onUpdateScoreboardTitle(title: String) {
        super.onUpdateScoreboardTitle(title)

        if (title == "§6§l烫手的山芋" && bot.script.status == Status.HUB) bot.script.status = Status.ROOM_WAIT_START
    }

}
