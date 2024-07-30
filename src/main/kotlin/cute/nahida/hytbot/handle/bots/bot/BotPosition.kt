package cute.nahida.hytbot.handle.bots.bot

import com.github.steveice10.mc.protocol.packet.ingame.server.entity.player.ServerPlayerPositionRotationPacket

@Suppress("MemberVisibilityCanBePrivate")
open class BotPosition (
    var x: Double = 0.0,
    var y: Double = 0.0,
    var z: Double = 0.0,
    var yaw: Float = 0F,
    var pitch: Float = 0F
) {
    fun setPosition(packet: ServerPlayerPositionRotationPacket) {
        this.x = packet.x
        this.y = packet.y
        this.z = packet.z

        this.yaw = packet.yaw
        this.pitch = packet.pitch 
    }

    override fun equals(other: Any?): Boolean {
        return when (other) {
            is BotPosition -> this.toString() == other.toString()
            is ServerPlayerPositionRotationPacket -> other.x == this.x && other.y == this.y && other.z == this.z && other.yaw == this.yaw && other.pitch == this.pitch
            else -> super.equals(other)
        }
    }

    override fun toString() = "xyz: $x, $y, $z   rotation: $yaw, $pitch"
    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        result = 31 * result + yaw.hashCode()
        result = 31 * result + pitch.hashCode()
        return result
    }
}