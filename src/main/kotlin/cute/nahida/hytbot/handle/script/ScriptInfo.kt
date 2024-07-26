package cute.nahida.hytbot.handle.script

data class ScriptInfo(
    /**
     * 模块名字
     */
    val superAccountMode: SuperAccountMode
) {
    enum class SuperAccountMode(val code: Int) {
        /**
         * 禁止配置 SuperAccount
         */
        DISABLE(0),
        /**
         * 只允许配置一个 SuperAccount
         */
        SINGLE(1),
        /**
         * 允许配置多个 SuperAccount
         */
        MULTI(2)
    }
}