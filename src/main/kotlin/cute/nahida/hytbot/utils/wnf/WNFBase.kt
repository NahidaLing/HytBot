package cute.nahida.hytbot.utils.wnf

object WNFBase {
    open class WNFGameAccount(
        val gameId: String,
        val gameType: Int,
        val roleName: String
    )
    class WNFProxy(
        val serverAddr: IpPort,
        val localProxy: IpPort
    ) {
        data class IpPort(
            val ip: String,
            val port: Int
        ) {
            init {
                require(ip.isNotEmpty()) { "IP address cannot be empty" }
                require(port in 1..65535) { "Port must be between 1 and 65535" }
            }

            override fun toString(): String {
                return "$ip:$port"
            }
        }
    }
}