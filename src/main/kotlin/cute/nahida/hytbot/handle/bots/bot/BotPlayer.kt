package cute.nahida.hytbot.handle.bots.bot

import com.github.steveice10.mc.auth.data.GameProfile
import java.util.*

/**
 * 机器入游戏内信息
 *
 * @param entityId 实体 ID
 * @param profiler 游戏 ID 和 UUID
 */
@Suppress("MemberVisibilityCanBePrivate")
open class BotPlayer (
    var entityId: Int = 0,
    var profiler: GameProfile = GameProfile(UUID.randomUUID(), ""),
)