package cute.nahida.hytbot.handle.script.impl

import cute.nahida.hytbot.handle.script.Script
import cute.nahida.hytbot.handle.script.ScriptInfo
import cute.nahida.hytbot.handle.script.manager.ScriptHytJoinGameData
import cute.nahida.hytbot.handle.script.utils.misc.Status

class ScriptHytWWolf: Script("WWolf", ScriptInfo(ScriptInfo.SuperAccountMode.MULTI)) {

    init {
        joinGameManager.game = ScriptHytJoinGameData(5, "LEISURE/ww-game")
    }

    /**
     * 由于 狼人杀游戏 自身问题
     * 玩家游戏开始后有概率被踢出
     *
     * 添加 startTime 并在 5s 后服务器无回应自动退出 可以避免这个问题 且还可以避免机器入被其他拿到狼人的玩家击杀
     */
    @JvmField
    var startTime: Long? = null

    override fun onUpdate() {
        super.onUpdate()

        startTime
            ?.takeIf { it + 8000L <= System.currentTimeMillis() }
            ?.also { bot.script.status = Status.ROOM_STARTED }
            ?.also { startTime = null }
    }

    override fun onMessage(msg: String) {
        super.onMessage(msg)

        if (msg.contains("[狼人杀Ⅱ] 游戏开始！")) startTime = System.currentTimeMillis()
    }

    override fun onTitle(title: String?, subTitle: String?) {
        super.onTitle(title, subTitle)

        when (title) {
            // 只需要让狼人立刻退出
            "§f本局你是..§c§l狼人!", "§e§l大吉大利!你获胜了!" -> bot.script.status = Status.ROOM_STARTED
        }
    }

    override fun onUpdateScoreboardTitle(title: String) {
        super.onUpdateScoreboardTitle(title)

        if (title.contains("狼人杀") && bot.script.status == Status.UNKNOWN) bot.script.status = Status.ROOM_WAIT_START
        if (title.contains("§c✿ §b§l花雨庭 §c✿")) startTime = null
    }

}
