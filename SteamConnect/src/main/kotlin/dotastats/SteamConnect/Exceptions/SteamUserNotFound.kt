package dotastats.SteamConnect.Exceptions

class SteamUserNotFound(steamId: Int) : SteamException("User with SteamID32 = $steamId not Found")