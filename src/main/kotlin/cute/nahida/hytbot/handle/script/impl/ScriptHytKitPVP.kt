package cute.nahida.hytbot.handle.script.impl

import com.github.steveice10.mc.protocol.data.game.window.ClickItemParam
import com.github.steveice10.mc.protocol.data.game.window.WindowAction
import com.github.steveice10.mc.protocol.data.game.window.WindowActionParam
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientWindowActionPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerOpenWindowPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerSetSlotPacket
import com.github.steveice10.packetlib.packet.Packet
import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.handle.bots.bot.Bot
import cute.nahida.hytbot.handle.bots.bot.BotPosition
import cute.nahida.hytbot.handle.script.Script
import cute.nahida.hytbot.handle.script.ScriptInfo
import cute.nahida.hytbot.handle.script.utils.misc.StaticCommands
import cute.nahida.hytbot.handle.script.utils.misc.Status

class ScriptHytKitPVP: Script("KitPVP", ScriptInfo(ScriptInfo.SuperAccountMode.MULTI)) {
    companion object {
        private val IN_SELECT_KIT_ROOM = BotPosition(-38.5, 212.0, 21.5, 269.90012f, 5.0f)
        private val IN_SELECT_KIT_ROOM_ANOTHER = BotPosition(-40.0, 213.0, 22.0, 270.0f, 5.0f)
        private val GOOD_POSITION = BotPosition(31.0, 65.0, -10.0, 0f, 0f)
    }

    private lateinit var bot: Bot

    private var coolDownJoin = 0

    override fun onStart(bot: Bot): Boolean {
        this.bot = bot
        return true
    }
    override fun onStop() {
        leave()
    }

    override fun onUpdate() {
        if (coolDownJoin > 0) coolDownJoin--

        bot.slot = 0

        when (bot.script.status) {
            Status.HUB -> join()
            Status.ROOM_WAIT_START -> bot.tryUseItem()
            Status.ROOM_STARTED -> { }
        }
    }

    override fun onTeleport(position: BotPosition) {
        when (position) {
            IN_SELECT_KIT_ROOM, IN_SELECT_KIT_ROOM_ANOTHER -> bot.script.status = Status.ROOM_WAIT_START
            GOOD_POSITION -> bot.script.status = Status.ROOM_STARTED
            else -> if (bot.script.status != Status.HUB) leave()
        }
    }

    override fun onTitle(title: String?, subTitle: String?) {
        when (title) {
            "花雨庭" -> bot.script.status = Status.HUB
        }
    }

    override fun onPacket(packet: Packet) {
        when (packet) {
            is ServerSetSlotPacket -> {
                // 匹配 钻石剑 且 不在玩家背包内  执行 点击
                if (packet.item?.id == 276 && packet.windowId != 0) {
                    bot.sendPacket(ClientWindowActionPacket(packet.windowId, 0, packet.slot, packet.item, WindowAction.CLICK_ITEM, ClickItemParam.LEFT_CLICK))
                }
            }
        }
    }

    private fun join() {
        if (bot.script.status == Status.HUB && coolDownJoin <= 0) {
            coolDownJoin = 20
            // 尝试打开游戏菜单
            bot.slot = 0
            bot.tryUseItem()
            Thread.sleep(100)
            // GermMod 执行 (WNF Only)
            @Suppress("SpellCheckingInspection")
            bot.sendMessage("/germclick c3ViamVjdF9maWdodA==")
            @Suppress("SpellCheckingInspection")
            bot.sendMessage("/germsubclick IMKnZcKnbOiBjOS4muaImOS6iQ==")
        }
    }

    private fun leave() {
        bot.script.status = Status.HUB
        coolDownJoin = 10
        bot.sendMessage(StaticCommands.COMMAND_HUB)
    }
}
