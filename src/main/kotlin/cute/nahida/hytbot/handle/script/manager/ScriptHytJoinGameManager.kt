package cute.nahida.hytbot.handle.script.manager

import cute.nahida.hytbot.handle.script.Script
import cute.nahida.hytbot.handle.script.ScriptInfo
import cute.nahida.hytbot.handle.script.utils.misc.StaticCommands
import cute.nahida.hytbot.handle.script.utils.packet.HytPacketUtils
import java.util.concurrent.locks.ReentrantLock

class ScriptHytJoinGameManager {

    lateinit var game: ScriptHytJoinGameData
    lateinit var script: Script

    private val lock = ReentrantLock()
    private val lockLeave = ReentrantLock()

    private var tryCount = 0

    /**
     * 尝试进入游戏 在执行前
     * 请确保已设置 game
     */
    fun join() {
        if (lock.tryLock()) {
            runCatching {
                tryCount++

                if (tryCount <= 2) {
                    script.bot.tryUseItem()

                    Thread.sleep(200)

                    script.bot.sendPacket(HytPacketUtils.generatePacketHytGermOpenSelectMenu())
                    script.bot.sendPacket(HytPacketUtils.generatePacketHytGermJoinGame(game))

                    Thread.sleep(1000)
                } else {
                    resetTryCount()
                    leave(force = true)
                    Thread.sleep(2000)
                }
            }
            lock.unlock()
        }
    }

    fun resetTryCount() {
        tryCount = 0
    }

    @JvmOverloads
    fun leave(force: Boolean = false) {
        if (lockLeave.tryLock()) {
            if (script.info.superAccountMode != ScriptInfo.SuperAccountMode.DISABLE && script.bot.script.superAccount && !force) {
                Thread.sleep(200)
            }

            script.bot.sendMessage(StaticCommands.COMMAND_HUB)
            lockLeave.unlock()
        }
    }
}