package cute.nahida.hytbot.handle.script

import cute.nahida.hytbot.handle.script.impl.*

class ScriptManager {
    private var scripts: HashMap<String, Class<out Script>> = hashMapOf()
    fun loadScripts() {
        scripts = arrayOf(
            ScriptHytPUBG::class.java,
            ScriptHytWWolf::class.java,
            ScriptHytBW8S::class.java, ScriptHytBW8D::class.java, ScriptHytBW44::class.java,
            ScriptHytSWS::class.java, ScriptHytSWD::class.java,
            ScriptHytKitPVP::class.java
        )
            .takeIf { it.isNotEmpty() }
            ?.map { it }
            ?.associateByTo(HashMap()) { (it.getDeclaredConstructor().newInstance() as Script).name  }
            ?: hashMapOf()
    }
    fun getScriptMap() = scripts
    fun get(name: String): Script? = scripts[name]?.getDeclaredConstructor()?.newInstance()
    fun container(name: String): Boolean = scripts.containsKey(name)
}
