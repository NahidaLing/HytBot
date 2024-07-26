package cute.nahida.hytbot.handle.script

import com.github.steveice10.packetlib.packet.Packet
import cute.nahida.hytbot.handle.bots.bot.Bot
import cute.nahida.hytbot.handle.bots.bot.BotPosition

abstract class Script(
    /**
     * 模块名字
     */
    val name: String,
    val info: ScriptInfo
) {
    /**
     * 开始运行
     *
     * @param bot 传入实例
     * @return 启动状态 true = 成功 false = 失败
     */
    open fun onStart(bot: Bot): Boolean = false
    /**
     * 停止运行
     */
    open fun onStop() { }
    /**
     * 每 200ms 调用一次
     */
    open fun onUpdate() { }
    /**
     * 传入聊天栏接受消息
     */
    open fun onMessage(msg: String) { }
    /**
     * 传入标题(/title命令)
     */
    open fun onTitle(title: String?, subTitle: String?) { }
    /**
     * 玩家被传送
     */
    open fun onTeleport(position: BotPosition) { }
    /**
     * 玩家进入游戏
     * ps: BungeeCord切换子服时也会执行
     */
    open fun onJoinGame() { }
    /**
     * 收到数据包时调用
     */
    open fun onPacket(packet: Packet) { }
}
