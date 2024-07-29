package cute.nahida.hytbot.handle.bots.bot

@Suppress("MemberVisibilityCanBePrivate")
open class BotPlayer (
    /**
     * 玩家实体Id
     */
    var entityId: Int = 0,
    /**
     * 玩家名称
     */
    var name: String = "",
    /**
     * 玩家花雨庭大厅等级
     */
    var hytLevel: Int = 0
)