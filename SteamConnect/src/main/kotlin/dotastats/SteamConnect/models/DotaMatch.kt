package dotastats.SteamConnect.models

import com.avenga.steamclient.model.steam.gamecoordinator.dota.match.DotaMatchDetails

class DotaMatch(data: DotaMatchDetails) {
    val matchId: Long = data.matchId
    val avgSkill: Int = data.averageSkill
    val cluster: Int = data.cluster
    val result = data.result
    // TODO: ADD MORE DATA
}