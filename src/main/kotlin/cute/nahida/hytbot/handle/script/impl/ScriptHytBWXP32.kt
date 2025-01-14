package cute.nahida.hytbot.handle.script.impl

import cute.nahida.hytbot.handle.script.manager.ScriptHytJoinGameData

@Suppress("SpellCheckingInspection")
open class ScriptHytBWXP32: ScriptHytBW8S("BWXP32") {
    init {
        joinGameManager.game = ScriptHytJoinGameData(5, "BEDWAR/bwxp-32")
    }
}
