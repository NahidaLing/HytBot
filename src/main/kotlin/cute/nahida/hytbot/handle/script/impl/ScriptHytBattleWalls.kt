package cute.nahida.hytbot.handle.script.impl

import cute.nahida.hytbot.handle.script.Script
import cute.nahida.hytbot.handle.script.ScriptInfo
import cute.nahida.hytbot.handle.script.manager.ScriptHytJoinGameData
import cute.nahida.hytbot.handle.script.utils.misc.Status

class ScriptHytBattleWalls: Script("BATTLE_WALLS", ScriptInfo(ScriptInfo.SuperAccountMode.SINGLE)) {

    init {
        @Suppress("SpellCheckingInspection")
        joinGameManager.game = ScriptHytJoinGameData(3, "TEAM_FIGHT/battlewalls")
    }

    override fun onUpdateScoreboardTitle(title: String) {
        super.onUpdateScoreboardTitle(title)

        if (title.contains("战墙") && bot.script.status == Status.UNKNOWN) bot.script.status = Status.ROOM_WAIT_START
    }

}
