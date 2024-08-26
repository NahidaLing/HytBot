package cute.nahida.hytbot.utils.wnf

@Suppress("PropertyName", "SpellCheckingInspection")
object WNFApiResponse {
    data class WNFApiResponseBase<T>(
        val code: Int,
        val entity: T,
        val message: String,
        val total: Int
    )
    data class WNFApiResponseListBase<T>(
        val code: Int,
        val entity: List<T>,
        val message: String,
        val total: Int
    )
    data class WNFUserData(
        val userId: Int,
        val username: String,
        val email: String,
        val rank: String,
        val registerTime: Long,
        val beToken: Int,
        val javaToken: Int,
        val javaSubscribe: Long,
        val beSubscribe: Int,
        /**
         * 设备码
         */
        val hardwareId: String
    )
    data class WNFGameAccount(
        var sauth: String,
        var user_id: Long
    )
    data class WNFGameAccountLogin(
        val code: Int,
        val message: String,
        val entity: EntityWrapper,
        val total: Int
    ) {
        data class EntityWrapper(
            val entity: EntityDetails,
            val statusCode: Int,
            val code: Int,
            val message: String
        ) {
            data class EntityDetails(
                // 省略掉 sa_data 字段
                val sauth_json: String?,
                val version: VersionInfo,
                val sdkuid: String,
                val aid: String,
                val hasMessage: Boolean,
                val hasGmail: Boolean,
                val otp_token: String?,
                val otp_pwd: String?,
                val token: String,
                val lock_time: Int,
                val env: String,
                val min_engine_version: String,
                val min_patch_version: String,
                val entity_id: String
            ) {
                data class VersionInfo(
                    val version: String?,
                    val launcher_md5: String,
                    val updater_md5: String
                )
            }
        }
    }
    data class WNFGameAccountRole(
        val game_id: String,
        val game_type: Int,
        val user_id: String,
        val name: String,
        val create_time: Long,
        val expire_time: Long,
        val entity_id: String
    )
}
