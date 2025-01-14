package cute.nahida.hytbot.handle.script.utils.packet

import com.github.steveice10.mc.protocol.packet.ingame.client.ClientPluginMessagePacket
import com.github.steveice10.packetlib.packet.Packet
import cute.nahida.hytbot.handle.script.manager.ScriptHytJoinGameData

object HytPacketUtils {
    @Suppress("SpellCheckingInspection")
    private val PACKET_PAYLOAD = "germmod-netease"
    private val PACKET_OPEN_GUI = byteArrayOf(0, 0, 0, 4, 0, 0, 0, 0, 0, 0, 0, 0, 8, 109, 97, 105, 110, 109, 101, 110, 117, 8, 109, 97, 105, 110, 109, 101, 110, 117, 8, 109, 97, 105, 110, 109, 101, 110, 117)
    private val PACKET_JOIN_GAME_PREFIX = byteArrayOf(0, 0, 0, 26, 20, 71, 85, 73, 36, 109, 97, 105, 110, 109, 101, 110, 117, 64, 101, 110, 116, 114, 121, 47)

    fun generatePacketHytGermOpenSelectMenu(): Packet {
        return ClientPluginMessagePacket(PACKET_PAYLOAD, PACKET_OPEN_GUI)
    }

    fun generatePacketHytGermJoinGame(data: ScriptHytJoinGameData): Packet {
        val json = "{\"entry\":${data.entry},\"sid\":\"${data.sid}\"}"

        val bytes = ByteArray(PACKET_JOIN_GAME_PREFIX.size + json.toByteArray().size + 2)
        System.arraycopy(PACKET_JOIN_GAME_PREFIX, 0, bytes, 0, PACKET_JOIN_GAME_PREFIX.size)
        bytes[PACKET_JOIN_GAME_PREFIX.size] = (48 + data.entry).toByte()
        bytes[PACKET_JOIN_GAME_PREFIX.size + 1] = json.length.toByte()
        System.arraycopy(json.toByteArray(), 0, bytes, PACKET_JOIN_GAME_PREFIX.size + 2, json.toByteArray().size)

        return ClientPluginMessagePacket(PACKET_PAYLOAD, bytes)
    }
}