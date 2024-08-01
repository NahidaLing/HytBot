package cute.nahida.hytbot.handle.bots.bot

import com.github.steveice10.mc.protocol.MinecraftProtocol
import com.github.steveice10.mc.protocol.data.game.entity.player.Hand
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerChangeHeldItemPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerUseItemPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientConfirmTransactionPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerTitlePacket
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerChangeHeldItemPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerConfirmTransactionPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerOpenWindowPacket
import com.github.steveice10.packetlib.Client
import com.github.steveice10.packetlib.event.session.DisconnectedEvent
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent
import com.github.steveice10.packetlib.event.session.SessionAdapter
import com.github.steveice10.packetlib.packet.Packet
import com.github.steveice10.packetlib.tcp.TcpSessionFactory
import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.handle.script.utils.misc.Status
import cute.nahida.hytbot.utils.math.RandomUtils

@Suppress("MemberVisibilityCanBePrivate")
class Bot (
    val id: String
) {
    /**
     * 是否废弃
     */
    var invalid = false
    /**
     * 是否需要重新连接
     */
    var needReconnect = false

    var script: BotBindScript = BotBindScript(this)

    var host = HytBot.configManager.configs.connect
    var port = 25565

    var player = BotPlayer()
    var position = BotPosition()

    var containerConfirmId = 1
    var inventoryConfirmId = 0

    private var checkIdMessage: String? = null

    lateinit var client: Client

    @Suppress("UNNECESSARY_SAFE_CALL", "USELESS_ELVIS")
    fun start(host: String = this.host, port: Int = this.port) {
        this@Bot.needReconnect = false

        this.host = host
        this.port = port

        client = Client(host, port, MinecraftProtocol(id), TcpSessionFactory())

        client.session.addListener(object : SessionAdapter() {
            override fun packetReceived(event: PacketReceivedEvent) {
                val packet = event.getPacket<Packet>()
                if (script.isEnable) script.bindScript?.onPacket(packet)
                when (packet) {
                    is ServerJoinGamePacket -> {
                        player.entityId = packet.entityId
                        HytBot.logger.info("[$id] 连接服务器成功 entityId: ${player.entityId}")
                        if (script.status == Status.HUB && player.name.isEmpty())  {
                            Thread {
                                Thread.sleep(5000) // 等待5秒 因为hyt大厅的大神发言冷却
                                checkIdMessage = "c_" + RandomUtils.random(1000,9999).toString()
                                sendMessage(checkIdMessage ?: "")
                            }.start()
                        }
                        if (script.isEnable) script.bindScript?.onJoinGame()
                    }
                    is ServerChatPacket -> {
                        val message = packet.message
                        HytBot.logger.info("[$id] ${message.fullText}")
                        checkIdMessage?.let {
                            // 通过正则匹配上方玩家自身发出的消息获取玩家ID和大厅等级
                            val regex = Regex("§e\\[lv(\\d+)]§r§f§7<§f(\\w+)§7> §7$it")

                            regex.find(message.fullText)?.let { matchResult ->
                                player.name = matchResult.groupValues[2]
                                player.hytLevel = matchResult.groupValues[1].toInt()
                                HytBot.logger.info("[$id] 上线成功 name: ${player.name}, hytLevel: ${player.hytLevel}")
                                checkIdMessage = null
                            }
                        }
                        if (script.isEnable) script.bindScript?.onMessage(message.fullText)
                    }
                    is ServerTitlePacket -> {
                        HytBot.logger.info("[$id] 标题信息: ${packet.title?.fullText} ${packet.subtitle?.fullText}")
                        if (script.isEnable) script.bindScript?.onTitle(packet.title?.fullText, packet.subtitle?.fullText)
                    }
                    is ServerPlayerPositionRotationPacket -> {
                        if (position != packet) {
                            position.setPosition(packet)
                            if (script.isEnable) script.bindScript?.onTeleport(position)
                            HytBot.logger.info("[$id] 玩家被传送  $position   teleportId: ${packet.teleportId}")
                        }
                    }
                    is ServerPlayerChangeHeldItemPacket -> {
                        HytBot.logger.info("[$id] 快捷栏变更: ${packet.slot}")
                        this@Bot.slot = packet.slot
                    }
                    is ServerConfirmTransactionPacket -> {
                        sendPacket(ClientConfirmTransactionPacket(packet.windowId,
                            packet.actionId,
                            true
                        ))
                    }
                    is ServerOpenWindowPacket -> {
                        HytBot.logger.info("[$id] 容器已打开: ${packet.windowId}")
                        containerConfirmId = 1
                    }
                }
            }

            override fun disconnected(event: DisconnectedEvent) {
                event?.cause.let {
                    HytBot.logger.warn("[$id] 断开连接: ${event.reason}", event.cause)
                } ?: run {
                    HytBot.logger.info("[$id] 断开连接: ${event.reason}")
                }
                this@Bot.needReconnect = when {
                    event.reason == BotsStaticText.DISCONNECT_BY_USER -> false
                    event.reason == "验证失败,请尝试重启启动器!" -> false
                    event.reason.startsWith("[封禁]") -> false
                    else -> true
                }
            }
        })

        HytBot.logger.info("[$id] 开始连接...")
        client.session.connect(false)
    }
    fun update(): Boolean {
        if (this@Bot.invalid) return false
        when {
            this@Bot.needReconnect -> {
                if (isConnected()) disconnect()
                HytBot.logger.info("[$id] 正在重新连接...")
                start()
            }
            isConnected() && script.isEnable -> script.bindScript?.onUpdate()
        }
        return true
    }

    fun isConnected() = try { client.session.isConnected } catch (_: Throwable) { false }
    fun disconnect() = client.session.disconnect(BotsStaticText.DISCONNECT_BY_USER)
    fun sendMessage(message: String): Boolean {
        if (message.isEmpty()) return false
        try {
            client.session.send(ClientChatPacket(message))
            return true
        } catch (_:Throwable) {
            return false
        }
    }
    fun sendPacket(packet: Packet): Boolean {
        try {
            client.session.send(packet)
            return true
        } catch (_:Throwable) {
            return false
        }
    }
    fun tryUseItem(hand: Hand = Hand.MAIN_HAND): Boolean {
        try {
            sendPacket(ClientPlayerUseItemPacket(hand))
            return true
        } catch (_: Throwable) {
            return false
        }
    }
    var slot: Int = 0
        set(value) {
            if (value in 0..8 && value != field) { // 合法的物体栏slot检测 并避免反复切换(GrimAC: BadPacketA)
                sendPacket(ClientPlayerChangeHeldItemPacket(slot))
                field = value
            }
        }
    fun getNextContainerConfirmActionId(): Int {
        containerConfirmId++
        return containerConfirmId
    }
    fun getNextInventoryConfirmActionId(): Int {
        inventoryConfirmId++
        return inventoryConfirmId
    }
}