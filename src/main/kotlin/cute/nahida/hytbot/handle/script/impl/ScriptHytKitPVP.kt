package cute.nahida.hytbot.handle.script.impl

import com.github.steveice10.mc.protocol.data.game.entity.metadata.ItemStack
import com.github.steveice10.mc.protocol.data.game.window.ClickItemParam
import com.github.steveice10.mc.protocol.data.game.window.DropItemParam
import com.github.steveice10.mc.protocol.data.game.window.ShiftClickItemParam
import com.github.steveice10.mc.protocol.data.game.window.WindowAction
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientConfirmTransactionPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientWindowActionPacket
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
        private val IN_SELECT_KIT_ROOM = mutableListOf(
            // 房间类型 A
            BotPosition(-38.5, 212.0, 21.5, 269.90012f, 5.0f),
            BotPosition(-40.0, 213.0, 22.0, 270.0f, 5.0f),
            // 房间类型 B
            BotPosition(15.5, 107.0, -2.5 ,0f ,0f),
            BotPosition(15.824555091786351, 107.0, -0.5095520156729517 ,270.0f, 5.0f)
        )
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
        when {
            IN_SELECT_KIT_ROOM.contains(position) -> {
                HytBot.logger.debug("[KitHelper] 回到选择职业大厅")
                bot.script.status = Status.ROOM_WAIT_START
            }
            GOOD_POSITION == position ->  {
                HytBot.logger.debug("[KitHelper] 进入")
                bot.script.status = Status.ROOM_STARTED
            }
            else -> if (bot.script.status != Status.HUB) leave()
        }
    }

    override fun onMessage(msg: String) {
        when (msg) {
            "§a§l无敌状态将于§c§l1§a§l秒后结束!" -> doRemoveArmor()
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
                when {
                    packet.item?.id == 276 && packet.windowId != 0 -> {
                        val actionId = bot.getNextContainerConfirmActionId()
                        bot.sendPacket(ClientWindowActionPacket(packet.windowId, actionId, packet.slot, packet.item, WindowAction.CLICK_ITEM, ClickItemParam.LEFT_CLICK))
                        bot.sendPacket(ClientConfirmTransactionPacket(packet.windowId, actionId, true))
                    }
                }
            }
        }
    }
    private fun doRemoveArmor() {
        for (i in 5..8) {
            val actionId = bot.getNextInventoryConfirmActionId()
            bot.sendPacket(ClientWindowActionPacket(0, actionId, i, ItemStack(0), WindowAction.SHIFT_CLICK_ITEM, ShiftClickItemParam.LEFT_CLICK))
            bot.sendPacket(ClientConfirmTransactionPacket(0, actionId, true))
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
