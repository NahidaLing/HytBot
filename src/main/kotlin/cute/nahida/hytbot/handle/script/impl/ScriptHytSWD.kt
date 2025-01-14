package cute.nahida.hytbot.handle.script.impl

import cute.nahida.hytbot.handle.script.manager.ScriptHytJoinGameData


open class ScriptHytSWD: ScriptHytSWS("SW-D") {
    init {
        @Suppress("SpellCheckingInspection")
        joinGameManager.game = ScriptHytJoinGameData(1, "SKYWAR/nskywar-double")
    }
}
