package cute.nahida.hytbot.handle.script.impl

import cute.nahida.hytbot.handle.script.Script
import cute.nahida.hytbot.handle.script.ScriptInfo
import cute.nahida.hytbot.handle.script.manager.ScriptHytJoinGameData
import cute.nahida.hytbot.handle.script.utils.misc.Status

open class ScriptHytSWS(name: String = "SW-S"): Script(name, ScriptInfo(ScriptInfo.SuperAccountMode.SINGLE)) {

    init {
        @Suppress("SpellCheckingInspection")
        joinGameManager.game = ScriptHytJoinGameData(0, "SKYWAR/nskywar")
    }

    override fun onMessage(msg: String) {
        super.onMessage(msg)

        @Suppress("SpellCheckingInspection")
        if (msg == "已为你自动开启 伤害显示, 输入 /shoff 关闭") bot.script.status = Status.ROOM_WAIT_START

    }

    override fun onTitle(title: String?, subTitle: String?) {
        super.onTitle(title, subTitle)

        if (subTitle == "§bFighting") bot.script.status = Status.ROOM_STARTED
    }

}
