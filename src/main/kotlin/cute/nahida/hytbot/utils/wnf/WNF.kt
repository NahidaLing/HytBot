package cute.nahida.hytbot.utils.wnf

import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import cute.nahida.hytbot.utils.HttpUtils

@Suppress("HttpUrlsUsage", "MemberVisibilityCanBePrivate", "SpellCheckingInspection")
class WNF(val url: String) {
    val header: HashMap<String, String> = hashMapOf(
        "Accept" to "application/json, text/plain, */*",
        "app_version" to "1.1.10.0740",

        "Accept-Language" to "zh-CN,zh;q=0.9,en;q=0.8",
        "Connection" to "keep-alive",
        "DNT" to "1"
    )

    fun login(user: String, password: String): Boolean {
        try {
            val body = JsonObject()

            body.addProperty("name", user)
            body.addProperty("pwd", password)

            val type = object : TypeToken<WNFApiResponse.WNFApiResponseBase<String>>() {}.type
            val response = Gson().fromJson<WNFApiResponse.WNFApiResponseBase<String>>(
                HttpUtils.post("http://${url}/api/login", body.toString(), header), type
            )

            header["access_token"] = response.entity
            return response.code == 0
        } catch (e: Throwable) {
            return false
        }
    }

    fun checkStatus(): Boolean {
        try {
            val type = object : TypeToken<WNFApiResponse.WNFApiResponseBase<String>>() {}.type
            val response = Gson().fromJson<WNFApiResponse.WNFApiResponseBase<String>>(
                HttpUtils.get("http://$url/api/getStatus", header), type
            )
            return response.entity == "alive"
        } catch (e: Throwable) {
            return false
        }
    }
    fun getUserData(): WNFApiResponse.WNFUserData? {
        try {
            val type = object : TypeToken<WNFApiResponse.WNFApiResponseBase<WNFApiResponse.WNFUserData>>() {}.type
            return Gson().fromJson(
                HttpUtils.get("http://$url/api/getUserData", header), type
            )
        } catch (e: Throwable) {
            return null
        }
    }
    fun getGameAccounts(): Array<WNFApiResponse.WNFGameAccount>? {
        try {
            val type = object : TypeToken<WNFApiResponse.WNFApiResponseBase<Array<WNFApiResponse.WNFGameAccount>>>() {}.type
            return Gson().fromJson<WNFApiResponse.WNFApiResponseBase<Array<WNFApiResponse.WNFGameAccount>>>(
                HttpUtils.get("http://$url/api/getGameAccounts", header), type
            ).entity
        } catch (e: Throwable) {
            return null
        }
    }
    fun loginGameAccount(account: WNFApiResponse.WNFGameAccount) = loginGameAccount(account.sauth)
    fun loginGameAccount(account: String): Boolean {
        try {
            val body = JsonObject()

            body.addProperty("sauth", account)

            val type = object : TypeToken<WNFApiResponse.WNFApiResponseBase<WNFApiResponse.WNFGameAccountLogin>>() {}.type
            return Gson().fromJson<WNFApiResponse.WNFApiResponseBase<WNFApiResponse.WNFGameAccountLogin>>(
                HttpUtils.post("http://$url/game/login", body.toString(), header), type
            ).code == 0
        } catch (e: Throwable) {
            return false
        }
    }
    fun getCurrentGameAccount(): String? {
        try {
            val type = object : TypeToken<WNFApiResponse.WNFApiResponseBase<String>>() {}.type
            val response = Gson().fromJson<WNFApiResponse.WNFApiResponseBase<String>>(
                HttpUtils.get("http://$url/game/getCurrentUser", header), type
            )
            return response.entity
        } catch (e: Throwable) {
            return null
        }
    }
    fun getRoleNames(gameId: String): MutableList<WNFBase.WNFGameAccount>? {
        try {
            val body = JsonObject()

            body.addProperty("game_id", gameId)

            val type = object : TypeToken<WNFApiResponse.WNFApiResponseBase<Array<WNFApiResponse.WNFGameAccountRole>>>() {}.type
            val response = Gson().fromJson<WNFApiResponse.WNFApiResponseBase<Array<WNFApiResponse.WNFGameAccountRole>>>(
                HttpUtils.post("http://$url/game/getRoleNames", body.toString(), header), type
            )
            val accounts: MutableList<WNFBase.WNFGameAccount> = mutableListOf()
            response.entity.forEach {
                accounts.add(WNFBase.WNFGameAccount(it.game_id, it.game_type, it.name))
            }
            return accounts
        } catch (e: Throwable) {
            e.printStackTrace()
            return null
        }
    }
    fun startProxy(role: WNFBase.WNFGameAccount, rentalServerPassword: String = "") = startProxy(role.gameId, role.gameType, role.roleName, rentalServerPassword)
    fun startProxy(gameId: String, gameType: Int, roleName: String, rentalServerPassword: String = ""): WNFBase.WNFProxy? {
        try {
            val body = JsonObject()

            body.addProperty("game_id", gameId)
            body.addProperty("game_type", gameType)
            body.addProperty("launch_type", 0)
            body.addProperty("rental_server_pwd", rentalServerPassword)
            body.addProperty("role_name", roleName)

            val type = object : TypeToken<WNFApiResponse.WNFApiResponseBase<String>>() {}.type
            val response = Gson().fromJson<WNFApiResponse.WNFApiResponseBase<String>>(
                HttpUtils.post("http://$url/game/startGame", body.toString(), header), type
            )
            // version 被抛弃
            val (originalIp, originalPort, targetIp, targetPort, _) = """(\S+):(\d+) ==> (\S+):(\d+) {2}游戏版本：(.*)""".toRegex().find(response.entity)?.destructured ?: return null

            return WNFBase.WNFProxy(WNFBase.WNFProxy.IpPort(originalIp, originalPort.toInt()), WNFBase.WNFProxy.IpPort(targetIp, targetPort.toInt()))
        } catch (e: Throwable) {
            e.printStackTrace()
            return null
        }
    }

    fun stopAllProxys(): Boolean {
        try {
            val type = object : TypeToken<WNFApiResponse.WNFApiResponseBase<String>>() {}.type
            val response = Gson().fromJson<WNFApiResponse.WNFApiResponseBase<String>>(
                HttpUtils.get("http://$url/api/stopAllProxys", header), type
            )
            return response.entity == "ok"
        } catch (e: Throwable) {
            return false
        }
    }
}