package dotastats.SteamConnect

import com.avenga.steamclient.base.ClientGCProtobufMessage
import com.avenga.steamclient.enums.SteamGame
import com.avenga.steamclient.generated.MsgGCHdrProtoBuf
import com.avenga.steamclient.model.steam.gamecoordinator.dota.account.DotaProfileCard
import com.avenga.steamclient.model.steam.gamecoordinator.dota.match.DotaMatchDetails
import com.avenga.steamclient.protobufs.dota.DotaGCMessagesClient
import com.avenga.steamclient.protobufs.dota.DotaGCMessagesId.EDOTAGCMsg
import com.avenga.steamclient.provider.UserCredentialsProvider
import com.avenga.steamclient.steam.client.SteamClient
import com.avenga.steamclient.steam.client.steamgamecoordinator.SteamGameCoordinator
import com.avenga.steamclient.steam.client.steamgamecoordinator.dota.DotaClient
import com.avenga.steamclient.steam.client.steamgameserver.SteamGameServer
import com.avenga.steamclient.steam.client.steamuser.LogOnDetails
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapter
import dotastats.SteamConnect.Exceptions.PlayerHiddenMatchHistory
import dotastats.SteamConnect.Exceptions.SteamException
import dotastats.SteamConnect.Exceptions.SteamUserNotFound
import dotastats.SteamConnect.Handlers.PlayerMatchHistoryHandler
import dotastats.SteamConnect.Handlers.PlayerStatsCallbackHandler
import dotastats.SteamConnect.models.*
import net.platinumdigitalgroup.jvdf.VDFParser
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.BufferedReader
import java.io.File
import java.util.*


@Suppress("SameParameterValue", "UNCHECKED_CAST")
class SteamConnect(username: String, password: String, private val apiKey: String, loadHeroesFile: String) {
    private val steamClient: SteamClient = SteamClient()
    private val gameServer = steamClient.getHandler(SteamGameServer::class.java)
    private val dotaClient = steamClient.getHandler(SteamGameCoordinator::class.java).getHandler(DotaClient::class.java)
    private val logOnDetails: LogOnDetails = LogOnDetails()
    private val timeoutInMillis = 15000L

    private val heroesMap: MutableMap<Int, DotaHero> = mutableMapOf()
    private val itemsMap: MutableMap<Int, DotaItem> = mutableMapOf()

    private val okHttpClient: OkHttpClient = OkHttpClient()
    private val moshi: Moshi = Moshi.Builder().build()

    @OptIn(ExperimentalStdlibApi::class)
    private val moshiAdapter = moshi.adapter<Map<String, *>>()

    init {
        loadHeroes(loadHeroesFile)
//        loadItemsFile(loadItemsFile)

        logOnDetails.username = username
        logOnDetails.password = password

        steamClient.credentialsProvider = UserCredentialsProvider(listOf(logOnDetails))

        steamClient.setOnAutoReconnect {
            try {
                gameServer.setClientPlayedGame(listOf(SteamGame.Dota2.applicationId), timeoutInMillis)
                dotaClient.sendClientHello(timeoutInMillis)
            } catch (e: Exception) {
                steamClient.reconnectOnUserInitiated = true
                steamClient.disconnect()
            }
        }
        steamClient.connectAndLogin()
    }

    fun getMatchDetails(matchId: Long) :  DotaMatchDetails {
        return dotaClient.getMatchDetails(matchId, timeoutInMillis).get()
    }

    fun getProfileCard(accountId: Int) : DotaProfileCard {
        return dotaClient.getAccountProfileCard(accountId, timeoutInMillis).get()
    }

    fun getPlayerStats(accountId: Int) : DotaPlayerStats {
        return getPlayerStatsRequest(accountId, timeoutInMillis).get()
    }

    fun getPlayerLastMatches(accountId: Int, matchesCount: Int) : MutableList<DotaMatchDetails> {
        val matchIds = getPlayerMatchHistory(accountId, matchesCount)
        val matches = mutableListOf<DotaMatchDetails>()

        matchIds.forEach {
            println(it.toLong())
            val match = dotaClient.getMatchDetails(it.toLong(), timeoutInMillis).get() // TODO: For some reason it causes timeout exception
            matches.add(match)
        }

        return matches
    }

    fun getUserData(accountId: Int) : SteamUser {
        val response = sendToWebAPI("ISteamUser", "GetPlayerSummaries", "v2", mapOf(
            "key" to this.apiKey,
            "steamids" to accountId + 76561197960265728,
        ))?.get("response") as Map<String, Any>
        val players = response["players"] as List<Map<String, Any>>
        if(players.isEmpty()) {
            throw SteamUserNotFound(accountId)
        }

        return SteamUser(players[0])
    }

    private fun getPlayerMatchHistory(accountId: Int, matchesCount: Int) : MutableList<Double> {
        val response = sendToWebAPI("IDOTA2Match_570", "GetMatchHistory", "v1", mapOf(
            "key" to this.apiKey,
            "matches_requested" to matchesCount,
            "account_id" to accountId,
        ))
        if (response?.isEmpty()!!) {
            throw PlayerHiddenMatchHistory(accountId)
        }

        val matchHistoryResponse = response["result"] as Map<String, *>

        val matches = matchHistoryResponse["matches"] as List<Map<* ,*>>
        val matchIds = mutableListOf<Double>()

        matches.forEach {
            matchIds.add(it["match_id"] as Double)
        }

        return matchIds
    }

    fun getHero(heroId: Int) : DotaHero? {
        return heroesMap[heroId]
    }

    private fun loadHeroes(fileName: String) {
        val bufferedReader: BufferedReader = File(fileName).bufferedReader()
        val data = bufferedReader.use { it.readText() }
        val vdf = VDFParser().parse(data)
        val heroes = vdf.getSubNode("DOTAHeroes")
        heroes.remove("Version")

        val heroNames = (sendToWebAPI("IEconDota2_570", "GetHeroes", "v1", mapOf(
            "key" to this.apiKey,
            "language" to "russian"
        ))?.get("result") as Map<String, String>)["heroes"] as List<Map<String, *>>
        for (heroMap in heroNames) {
            val hero = heroes.getSubNode(heroMap["name"] as String)
            heroesMap[hero.getInt("HeroID")] = DotaHero(hero, heroMap["name"] as String, heroMap["localized_name"] as String)
        }
    }

    private fun sendToWebAPI(interfaceName: String, methodName: String, version: String, args: Map<String, *>) : Map<String, *>? {
        val request = Request.Builder()
            .url("https://api.steampowered.com/$interfaceName/$methodName/$version/${convertArgsToString(args)}")
            .build()

        try {
            val httpResponse = okHttpClient.newCall(request).execute()
            val response = moshiAdapter.fromJson(httpResponse.body?.string()!!)
            return response
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun convertArgsToString(args: Map<String, *>) : String {
        val stringBuilder = StringBuilder().append("?")
        args.forEach {
            stringBuilder.append(it.key).append("=").append(it.value).append("&")
        }
        return stringBuilder.substring(0, stringBuilder.length - 1).toString()
    }

    private fun getPlayerStatsRequest(accountId: Int, timeout: Long) : Optional<DotaPlayerStats> {
        val jobId = dotaClient.client.nextJobID.value
        val playerStatsCallback = dotaClient.client.addGCCallbackToQueue(EDOTAGCMsg.k_EMsgGCToClientPlayerStatsResponse.number, dotaClient.applicationId, jobId)
        val playerStatsMessage = ClientGCProtobufMessage(DotaGCMessagesClient.CMsgClientToGCPlayerStatsRequest::class.java, EDOTAGCMsg.k_EMsgClientToGCPlayerStatsRequest.number)
        (playerStatsMessage.body as DotaGCMessagesClient.CMsgClientToGCPlayerStatsRequest.Builder).accountId = accountId
        (playerStatsMessage.header as MsgGCHdrProtoBuf).proto.jobidSource = jobId
        dotaClient.gameCoordinator.send(playerStatsMessage, dotaClient.applicationId, EDOTAGCMsg.k_EMsgClientToGCPlayerStatsRequest)
        return PlayerStatsCallbackHandler.handle(playerStatsCallback, timeout, dotaClient.client)
    }

    fun stopClient() {
        this.steamClient.disconnect()
    }
}