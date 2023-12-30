package dotastats.SteamConnect.models

import com.avenga.steamclient.protobufs.dota.DotaGCMessagesClient

class DotaPlayerStats(builder: DotaGCMessagesClient.CMsgGCToClientPlayerStatsResponse.Builder) {
    // avg
    val avgGpm = builder.meanGpm
    val avgXPpm = builder.meanXppm
    val avgNetWorth = builder.meanNetworth
    val avgDamage = builder.meanDamage
    val avgHeal = builder.meanHeals
    val avgLastHits = builder.meanLasthits

    // stats
    val rampages = builder.rampages
    val tripleKills = builder.tripleKills
    val firstBloodClaimed = builder.firstBloodClaimed
    val firstBloodGiven = builder.firstBloodGiven
    val couriersKilled = builder.couriersKilled
    val aegisesSnatched = builder.aegisesSnatched
    val cheesesEaten = builder.cheesesEaten
    val creepsStacked = builder.creepsStacked
    val rapiersPurchased = builder.rapiersPurchased

    // scores
    val fightScore = builder.fightScore
    val farmScore = builder.farmScore
    val supportScore = builder.supportScore
    val pushScore = builder.pushScore
    val versatilityScore = builder.versatilityScore
}