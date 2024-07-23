package cute.nahida.hytbot.handle.update

import cute.nahida.hytbot.HytBot
import java.util.concurrent.CopyOnWriteArrayList

@Suppress("MemberVisibilityCanBePrivate")
class UpdateManager {
    private val updates: CopyOnWriteArrayList<() -> Boolean> = CopyOnWriteArrayList()

    private var invalid = false

    private val updateThread = Thread {
        HytBot.logger.error("[Update] 线程已启动")
        while (!invalid) {
            update()
            Thread.sleep(200)
        }
    }

    fun reload() {
        if (updateThread.isAlive) return

        invalid = false
        updateThread.start()
    }
    fun stop() {
        invalid = true

        HytBot.logger.info("[Update] 线程已停止")
    }
    fun addUpdate(add: () -> Boolean) {
        if (!updates.contains(add)) {
            updates.add(add)
            HytBot.logger.debug("[Update] 新增任务: ${add.javaClass.name}")
        }

    }
    fun update() {
        try {
            updates.removeIf { !it() }
        } catch (e: Throwable) {
            HytBot.logger.info("[Update] 执行时发生异常", e)
        }
    }
}
