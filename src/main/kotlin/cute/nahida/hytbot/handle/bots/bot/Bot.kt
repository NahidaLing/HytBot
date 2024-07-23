package cute.nahida.hytbot.handle.bots.bot

import com.github.steveice10.mc.protocol.MinecraftProtocol
import com.github.steveice10.mc.protocol.data.game.entity.player.Hand
import com.github.steveice10.mc.protocol.data.message.Message
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerChangeHeldItemPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerMovementPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerPositionRotationPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerRotationPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerUseItemPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientConfirmTransactionPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.world.ClientTeleportConfirmPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerTitlePacket
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.ServerEntityTeleportPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerChangeHeldItemPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerConfirmTransactionPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerSetSlotPacket
import com.github.steveice10.packetlib.Client
import com.github.steveice10.packetlib.event.session.DisconnectedEvent
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent
import com.github.steveice10.packetlib.event.session.PacketSendingEvent
import com.github.steveice10.packetlib.event.session.SessionAdapter
import com.github.steveice10.packetlib.packet.Packet
import com.github.steveice10.packetlib.tcp.TcpSessionFactory
import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.handle.script.Script
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

    var script: BotBindScript = BotBindScript()

    var host = HytBot.configManager.configs.connect
    var port = 25565

    var position = BotPosition()

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
                script.bindScript?.onPacket(packet)
                when (packet) {
                    is ServerJoinGamePacket -> {
                        HytBot.logger.info("[$id] 连接服务器成功")
                        script.bindScript?.onJoinGame()
                    }
                    is ServerChatPacket -> {
                        val message = packet.message
                        HytBot.logger.info("[$id] ${message.fullText}")
                        script.bindScript?.onMessage(message.fullText)
                    }
                    is ServerTitlePacket -> {
                        HytBot.logger.info("[$id] 标题信息: ${packet.title?.fullText} ${packet.subtitle?.fullText}")
                        script.bindScript?.onTitle(packet.title?.fullText, packet.subtitle?.fullText)
                    }
                    is ServerPlayerPositionRotationPacket -> {
                        if (position != packet) {
                            script.bindScript?.onTeleport(position)
                            HytBot.logger.info("[$id] 玩家被传送 xyz: ${packet.x}, ${packet.y}, ${packet.z}   rotation: ${packet.yaw}, ${packet.pitch}   teleportId: ${packet.teleportId}")
                            position.setPosition(packet)
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
                }
            }

            override fun disconnected(event: DisconnectedEvent) {
                event?.cause.let {
                    HytBot.logger.warn("[$id] 断开连接: ${event.reason}", event.cause)
                } ?: run {
                    HytBot.logger.info("[$id] 断开连接: ${event.reason}")
                }
                if (event.reason != BotsStaticText.DISCONNECT_BY_USER) {
                    this@Bot.needReconnect = true
                }
            }
        })

        HytBot.logger.info("[$id] 开始连接...")
        client.session.connect(false)
        HytBot.updateManager.addUpdate { update() }
    }
    private fun update(): Boolean {
        if (this@Bot.invalid) return false
        if (this@Bot.needReconnect) {
            if (isConnected()) disconnect()
            HytBot.logger.info("[$id] 正在重新连接...")
            start()
        } else {
            script.bindScript?.onUpdate()
        }
        return true
    }
    fun isConnected() = client.session.isConnected
    fun disconnect() = client.session.disconnect(BotsStaticText.DISCONNECT_BY_USER)
    fun sendMessage(message: String): Boolean {
        try {
            when (message) {
                "useitem" ->  client.session.send(ClientPlayerUseItemPacket(Hand.MAIN_HAND))
            }
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
}