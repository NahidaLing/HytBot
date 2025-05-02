package cute.nahida.hytbot.handle.script.impl

import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientTabCompletePacket
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerTabCompletePacket
import com.github.steveice10.packetlib.packet.Packet
import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.handle.bots.bot.Bot
import cute.nahida.hytbot.handle.script.Script
import cute.nahida.hytbot.handle.script.ScriptInfo
import cute.nahida.hytbot.handle.script.manager.ScriptHytJoinGameData
import cute.nahida.hytbot.handle.script.utils.misc.Status

open class ScriptHytSWS(name: String = "SW-S"): Script(name, ScriptInfo(ScriptInfo.SuperAccountMode.SINGLE)) {

    companion object {
        var lastSuperAccount: Bot? = null

        fun isInEloBoosterMode() = HytBot.botsManager.bots.values
            .filter { it.script.bindScript is ScriptHytSWS }
            .any { it.script.superAccount }
    }

    init {
        @Suppress("SpellCheckingInspection")
        joinGameManager.game = ScriptHytJoinGameData(0, "SKYWAR/nskywar")
    }

    override fun onMessage(msg: String) {
        super.onMessage(msg)

        @Suppress("SpellCheckingInspection")
        if (msg == "已为你自动开启 伤害显示, 输入 /shoff 关闭") {
            bot.script.status = Status.ROOM_WAIT_START

            if (lastSuperAccount == null || !HytBot.botsManager.bots.values.contains(lastSuperAccount)) {
                lastSuperAccount = HytBot.botsManager.bots.values
                    .filter { it.script.bindScript is ScriptHytSWS }
                    .filter { it.script.superAccount }
                    .randomOrNull()
                    ?.also { HytBot.logger.info("[SWHelper] 当前主号: ${it.player.profiler.name}") }
            }

        }
        if (msg == "你现在是观察者状态. 按E打开菜单.") bot.script.status = Status.ROOM_STARTED
        if (msg == "开始倒计时: 1 秒" && bot === lastSuperAccount) bot.sendPacket(ClientTabCompletePacket(" ", false, Position(-1, -1, -1)))
        if (msg.contains("中赢得了")) HytBot.logger.info("[SWHelper] +${msg.substringAfter("(+").substringBefore(" Elo").toIntOrNull()} Elo")

    }

    override fun onTitle(title: String?, subTitle: String?) {
        super.onTitle(title, subTitle)

        if (subTitle == "§bFighting" && isInEloBoosterMode()) {
            if (shouldLoggerMsg()) {
                lastSuperAccount = null
                joinGameManager.leaveDelay = 2000L
            } else {
                joinGameManager.leaveDelay = 0L
            }
            bot.script.status = Status.ROOM_STARTED
        }
    }

    override fun onPacket(packet: Packet) {
        when (packet) {
            is ServerTabCompletePacket -> HytBot.logger.info("[SWHelper] 本局玩家列表: ${packet.matches.joinToString(", ")}")
        }
        super.onPacket(packet)
    }

    override fun shouldLoggerMsg() = bot === lastSuperAccount

}
