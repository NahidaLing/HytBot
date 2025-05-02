package cute.nahida.hytbot.handle.bots.bot

import com.github.steveice10.mc.protocol.MinecraftProtocol
import com.github.steveice10.mc.protocol.data.game.entity.player.Hand
import com.github.steveice10.mc.protocol.data.game.entity.player.PositionElement
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientChatPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.ClientPluginMessagePacket
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerChangeHeldItemPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.player.ClientPlayerUseItemPacket
import com.github.steveice10.mc.protocol.packet.ingame.client.window.ClientConfirmTransactionPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerChatPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerJoinGamePacket
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerRespawnPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.ServerTitlePacket
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerChangeHeldItemPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.scoreboard.ServerScoreboardObjectivePacket
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerConfirmTransactionPacket
import com.github.steveice10.mc.protocol.packet.ingame.server.window.ServerOpenWindowPacket
import com.github.steveice10.mc.protocol.packet.login.server.LoginSuccessPacket
import com.github.steveice10.packetlib.Client
import com.github.steveice10.packetlib.event.session.DisconnectedEvent
import com.github.steveice10.packetlib.event.session.PacketReceivedEvent
import com.github.steveice10.packetlib.event.session.SessionAdapter
import com.github.steveice10.packetlib.packet.Packet
import com.github.steveice10.packetlib.tcp.TcpSessionFactory
import cute.nahida.hytbot.HytBot
import cute.nahida.hytbot.handle.bots.BotsManager
import cute.nahida.hytbot.handle.script.utils.misc.Status
import net.darkmeow.irc.client.IRCClient
import net.darkmeow.irc.client.enums.EnumResultLogin
import net.darkmeow.irc.client.interfaces.IRCClientProvider
import net.darkmeow.irc.client.listener.IRCClientListenableSimple
import net.darkmeow.irc.client.network.IRCClientOptions
import net.darkmeow.irc.data.ClientBrandData
import net.darkmeow.irc.data.DataSessionOptions
import net.darkmeow.irc.data.PlayerSessionData

@Suppress("MemberVisibilityCanBePrivate")
class Bot (
    val manager: BotsManager,
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

    var irc: IRCClientProvider? = null

    var containerConfirmId = 1
    var inventoryConfirmId = 0

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
                    is LoginSuccessPacket -> {
                        HytBot.logger.info("[$id] 连接服务器成功    ${packet.profile.name} ${packet.profile.id}")
                        player.profiler = packet.profile

                        if (manager.base.configManager.configs.irc.enable && irc == null) {
                            irc = IRCClient.newInstance(
                                IRCClientListenableSimple(),
                                IRCClientOptions(
                                    manager.base.configManager.configs.irc.server.host,
                                    manager.base.configManager.configs.irc.server.port,
                                    manager.base.configManager.configs.irc.server.key
                                )
                            )
                        }

                        if (manager.base.configManager.configs.irc.enable) {
                            Thread {
                                irc
                                    ?.apply {
                                        connect()
                                        login(
                                            manager.base.configManager.configs.irc.login.name,
                                            manager.base.configManager.configs.irc.login.token,
                                            ClientBrandData(
                                                manager.base.configManager.configs.irc.login.brand.id,
                                                manager.base.configManager.configs.irc.login.brand.hash,
                                                manager.base.configManager.configs.irc.login.brand.version_id,
                                                manager.base.configManager.configs.irc.login.brand.version_name
                                            ),
                                            false
                                        )
                                            ?.takeIf { it == EnumResultLogin.SUCCESS }
                                            ?.also {
                                                uploadSessionOptions(
                                                    DataSessionOptions(
                                                        PlayerSessionData(player.profiler.name, player.profiler.id),
                                                        null,
                                                        null,
                                                        1000000,
                                                        "§a",
                                                        true
                                                    )
                                                )
                                                HytBot.logger.info("[$id] IRC 服务器连接成功")
                                            }
                                            ?: run {
                                                HytBot.logger.warn("[$id] IRC 服务器连接失败")
                                            }
                                    }
                            }.start()
                        }
                    }
                    is ServerJoinGamePacket -> {
                        script.status = Status.UNKNOWN
                        player.entityId = packet.entityId

                        containerConfirmId = 1
                        inventoryConfirmId = 0

                        if (manager.base.configManager.configs.bot.hackerBrand) {
                            sendPacket(ClientPluginMessagePacket("REGISTER", mutableListOf("FML|HS", "FML", "FML|MP", "Forge", "armourers", "hyt0", "germplugin-netease", "VexView").joinToString("\u0000").toByteArray(Charsets.UTF_8)))
                            sendPacket(ClientPluginMessagePacket("MC|Brand", "Forge".toByteArray(Charsets.UTF_8)))
                        }

                        if (script.isEnable) script.bindScript?.onJoinGame()
                    }
                    is ServerRespawnPacket -> {
                        containerConfirmId = 1
                        inventoryConfirmId = 0
                    }
                    is ServerChatPacket -> {
                        if (script.bindScript?.shouldLoggerMsg() ?: script.superAccount) HytBot.logger.info("[$id] ${packet.message.fullText}")
                        if (script.isEnable) script.bindScript?.onMessage(packet.message.fullText)
                    }
                    is ServerTitlePacket -> if (script.isEnable) script.bindScript?.onTitle(packet.title?.fullText, packet.subtitle?.fullText)
                    is ServerPlayerPositionRotationPacket -> {
                        var posX = packet.x
                        var posY = packet.y
                        var posZ = packet.z
                        var yaw = packet.yaw
                        var pitch = packet.pitch

                        if (packet.relativeElements.contains(PositionElement.X)) posX += position.x
                        if (packet.relativeElements.contains(PositionElement.Y)) posY += position.y
                        if (packet.relativeElements.contains(PositionElement.Z)) posZ += position.z
                        if (packet.relativeElements.contains(PositionElement.YAW)) yaw += position.yaw
                        if (packet.relativeElements.contains(PositionElement.PITCH)) pitch += position.pitch

                        position.x = posX
                        position.y = posY
                        position.z = posZ
                        position.yaw = yaw
                        position.pitch = pitch
                        position.fixPosition()

                        // 666还能这样写
                        // sendPacket(ClientTeleportConfirmPacket(packet.teleportId))
                        // sendPacket(ClientPlayerPositionRotationPacket(false, position.x + 9999, position.y, position.z, position.yaw, position.pitch))

                        if (script.isEnable && packet.relativeElements.isEmpty()) script.bindScript?.onTeleport(position)
                    }
                    is ServerPlayerChangeHeldItemPacket -> this@Bot.slot = packet.slot
                    is ServerConfirmTransactionPacket -> {
                        sendPacket(ClientConfirmTransactionPacket(packet.windowId,
                            packet.actionId,
                            true
                        ))
                    }
                    is ServerOpenWindowPacket -> containerConfirmId = 1
                    is ServerScoreboardObjectivePacket -> if (script.isEnable) script.bindScript?.onUpdateScoreboardTitle(packet.displayName ?: "")
                }
            }

            override fun disconnected(event: DisconnectedEvent) {
                player.entityId = 0

                event?.cause.let {
                    HytBot.logger.warn("[$id] 断开连接: ${event.reason}", event.cause)
                } ?: run {
                    HytBot.logger.info("[$id] 断开连接: ${event.reason}")
                }

                this@Bot.needReconnect = when {
                    event.reason == BotsStaticText.DISCONNECT_BY_USER -> false
                    event.reason.contains("失败") -> false
                    event.reason.contains("封禁") -> false
                    event.reason.contains("Connection refused") -> false
                    else -> true
                }

                irc?.disconnect()
            }
        })

        HytBot.logger.info("[$id] 开始连接...")
        synchronized(manager) {
            client.session.connect(true)
            Thread.sleep(manager.base.configManager.configs.bot.loginDelay)
        }

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