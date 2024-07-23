package cute.nahida.hytbot.handle.script.utils.misc

enum class Status(val code: Int) {
    /**
     * 大厅
     */
    HUB(0),
    /**
     * 房间内等待开始
     */
    ROOM_WAIT_START(1),
    /**
     * 房间内已经开始
     */
    ROOM_STARTED(2)
}