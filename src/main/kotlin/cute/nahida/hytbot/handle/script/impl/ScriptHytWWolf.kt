package cute.nahida.hytbot.handle.script.impl

import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.handle.script.Script
import cute.nahida.hytbot.handle.script.ScriptInfo
import cute.nahida.hytbot.handle.script.manager.ScriptHytJoinGameData
import cute.nahida.hytbot.handle.script.utils.misc.Status

class ScriptHytWWolf: Script("WWolf", ScriptInfo(ScriptInfo.SuperAccountMode.MULTI)) {

    init {
        joinGameManager.game = ScriptHytJoinGameData(5, "LEISURE/ww-game")
    }

    override fun onMessage(msg: String) {
        when {
            msg.contains("[狼人杀Ⅱ]") && msg.contains("加入了游戏") -> bot.script.status = Status.ROOM_WAIT_START
            msg == "[狼人杀Ⅱ] 游戏开始！" -> if (!bot.script.superAccount) bot.sendMessage(HytBot.configManager.configs.message.on_game_started)
        }
    }

    override fun onTitle(title: String?, subTitle: String?) {
        super.onTitle(title, subTitle)

        when (title) {
            // 只需要让狼人立刻退出
            "§f本局你是..§c§l狼人!", "§e§l大吉大利!你获胜了!" -> bot.script.status = Status.ROOM_STARTED
        }
    }

}
