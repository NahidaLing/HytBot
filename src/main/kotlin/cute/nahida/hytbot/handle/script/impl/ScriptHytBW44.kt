package cute.nahida.hytbot.handle.script.impl

import cute.nahida.hytbot.handle.script.manager.ScriptHytJoinGameData


open class ScriptHytBW44: ScriptHytBW8S("BW44") {
    init {
        @Suppress("SpellCheckingInspection")
        joinGameManager.game = ScriptHytJoinGameData(3, "BEDWAR/bw-team")
    }
}
