package cute.nahida.hytbot.handle.script.impl

import cute.nahida.hytbot.handle.script.manager.ScriptHytJoinGameData


open class ScriptHytBW8D: ScriptHytBW8S("BW8D") {
    init {
        @Suppress("SpellCheckingInspection")
        joinGameManager.game = ScriptHytJoinGameData(2, "BEDWAR/bw-double")
    }
}
