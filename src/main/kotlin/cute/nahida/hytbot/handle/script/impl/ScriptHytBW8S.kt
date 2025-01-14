package cute.nahida.hytbot.handle.script.impl

import cute.nahida.hytbot.handle.bots.bot.BotPosition
import cute.nahida.hytbot.handle.script.Script
import cute.nahida.hytbot.handle.script.ScriptInfo
import cute.nahida.hytbot.handle.script.manager.ScriptHytJoinGameData
import cute.nahida.hytbot.handle.script.utils.misc.Status

open class ScriptHytBW8S(name: String = "BW8S"): Script(name, ScriptInfo(ScriptInfo.SuperAccountMode.SINGLE)) {
    init {
        @Suppress("SpellCheckingInspection")
        joinGameManager.game = ScriptHytJoinGameData(1, "BEDWAR/bw-solo")
    }

    override fun onMessage(msg: String) {
        when (msg) {
            //"起床战争>> 游戏开始 ..." -> bot.script.status = Status.ROOM_STARTED
        }
    }

    override fun onTitle(title: String?, subTitle: String?) {
        super.onTitle(title, subTitle)

        if (title?.contains("加入了游戏") == true) {
            bot.script.status = Status.ROOM_WAIT_START
        }
    }
}
