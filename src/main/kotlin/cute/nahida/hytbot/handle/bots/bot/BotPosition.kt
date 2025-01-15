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
    fun fixPosition() {
        this.x = this.x.coerceIn(-3.0E7..3.0E7)
        this.z = this.z.coerceIn(-3.0E7..3.0E7)

        this.yaw %= 360F
        this.pitch %= 360F
        this.pitch = this.pitch.coerceIn(-90F..90F)
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