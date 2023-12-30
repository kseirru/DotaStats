package dotastats.SteamConnect.models

class SteamUser(data: Map<String, Any>) {
    val steamId: Long = (data["steamid"] as String).toLong()
    val accountId: Int = (steamId - 76561197960265728).toInt()
    val username: String = data["personaname"] as String
    val profileUrl: String = "https://steamcommunity.com/profiles/$steamId"
    val avatarUrl: String = data["avatarfull"].takeIf { it != null }.toString()
}