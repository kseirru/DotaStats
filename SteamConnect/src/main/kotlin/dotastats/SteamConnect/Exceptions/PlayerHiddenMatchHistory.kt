package dotastats.SteamConnect.Exceptions

class PlayerHiddenMatchHistory(accountId: Int) : SteamException("No matches found for user with SteamID32 $accountId.")