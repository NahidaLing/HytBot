package cute.nahida.hytbot.handle.script.impl

import cute.nahida.hytbot.handle.script.manager.ScriptHytJoinGameData

@Suppress("SpellCheckingInspection")
open class ScriptHytBWXP16: ScriptHytBW8S("BWXP16") {
    init {
        joinGameManager.game = ScriptHytJoinGameData(4, "BEDWAR/bwxp16new")
    }
}
