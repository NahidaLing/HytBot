package cute.nahida.hytbot.handle.script.manager

import cute.nahida.hytbot.handle.script.Script
import cute.nahida.hytbot.handle.script.utils.packet.HytPacketUtils
import java.util.concurrent.locks.ReentrantLock

class ScriptHytJoinGameManager {

    lateinit var game: ScriptHytJoinGameData
    lateinit var script: Script

    private val lock = ReentrantLock()

    private var tryCount = 0

    /**
     * 尝试进入游戏 在执行前
     * 请确保已设置 game
     */
    fun join() {
        if (lock.tryLock()) {
            runCatching {
                tryCount++

                if (tryCount < 4) {
                    script.bot.tryUseItem()

                    Thread.sleep(200)

                    script.bot.sendPacket(HytPacketUtils.generatePacketHytGermOpenSelectMenu())
                    script.bot.sendPacket(HytPacketUtils.generatePacketHytGermJoinGame(game))

                    Thread.sleep(1000)
                } else {
                    resetTryCount()
                    script.onStop()
                    Thread.sleep(2000)
                }
            }
            lock.unlock()
        }
    }

    fun resetTryCount() {
        tryCount = 0
    }
}