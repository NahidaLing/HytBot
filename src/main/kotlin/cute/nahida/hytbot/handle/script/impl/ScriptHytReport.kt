package cute.nahida.hytbot.handle.script.impl

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack
import com.github.steveice10.mc.protocol.data.game.entity.metadata.Position
import com.github.steveice10.mc.protocol.data.game.window.ClickItemParam
import com.github.steveice10.mc.protocol.data.game.window.WindowAction
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientTabCompletePacket
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientConfirmTransactionPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientWindowActionPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerTabCompletePacket
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerOpenWindowPacket
import com.github.steveice10.packetlib.packet.Packet
import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.handle.script.Script
import cute.nahida.hytbot.handle.script.ScriptInfo

class ScriptHytReport: Script("Report", ScriptInfo(ScriptInfo.SuperAccountMode.DISABLE)) {
    companion object {
        val REGEX_REPORT_NAME = Regex("""我们已经收到你对玩家(.*)的举报，谢谢！""")
        val REGEX_REPORT_ID = Regex("""举报成功！举报编号(.*)""")
    }

    var aliveTick = 0
    var lastHub = false

    override fun onUpdate() {
        super.onUpdate()

        aliveTick++
        if (aliveTick % 5 == 0) updateHub()
    }

    fun updateHub() {
        lastHub = !lastHub
        bot.sendMessage("/hub ${if (lastHub) "1" else "0"}")
    }

    override fun onUpdateScoreboardTitle(title: String) {
        super.onUpdateScoreboardTitle(title)

        if (title.contains("§c✿ §b§l花雨庭 §c✿")) bot.sendPacket(ClientTabCompletePacket(" ", false, Position(-1, -1, -1)))
    }

    @JvmField
    var lastReportName = ""

    @JvmField
    var lastReportId = ""

    override fun onMessage(msg: String) {
        super.onMessage(msg)

        REGEX_REPORT_NAME
            .find(msg)
            ?.groupValues
            ?.getOrNull(1)
            ?.also {
                lastReportName = it
            }

        REGEX_REPORT_ID
            .find(msg)
            ?.groupValues
            ?.getOrNull(1)
            ?.also {
                lastReportId = it
                HytBot.logger.info("[ReportSpam] 举报成功 $lastReportName (举报编号:${lastReportId})")
                updateHub()
            }
    }

    override fun onPacket(packet: Packet) {
        when (packet) {
            is ServerTabCompletePacket -> bot.sendMessage("/report ${packet.matches.randomOrNull() ?: ""}")
            is ServerOpenWindowPacket -> if (packet.name.contains("请选择举报理由")) {
                val actionId = bot.getNextContainerConfirmActionId()

                bot.sendMessage("L") // 大厅发言大概率发不出去
                bot.sendPacket(ClientWindowActionPacket(packet.windowId, actionId, 24, ItemStack(0), WindowAction.CLICK_ITEM, ClickItemParam.LEFT_CLICK))
                bot.sendPacket(ClientConfirmTransactionPacket(packet.windowId, actionId, true))
            }
        }

        super.onPacket(packet)
    }

}
