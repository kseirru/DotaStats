package dotastats.SteamConnect

import com.avenga.steamclient.base.ClientGCProtobufMessage
import com.avenga.steamclient.enums.SteamGame
import com.avenga.steamclient.exception.CallbackTimeoutException
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
import dotastats.SteamConnect.models.DotaPlayerStats
import dotastats.SteamConnect.models.PlayerStatsCallbackHandler
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.*
import java.util.function.Consumer


@Suppress("SameParameterValue")
class SteamConnect(username: String, password: String, private val apiKey: String) {
    private val steamClient: SteamClient = SteamClient()
    private val gameServer = steamClient.getHandler(SteamGameServer::class.java)
    private val dotaClient = steamClient.getHandler(SteamGameCoordinator::class.java).getHandler(DotaClient::class.java)
    private val logOnDetails: LogOnDetails = LogOnDetails()
    private val timeoutInMillis = 15000L

    private val okHttpClient: OkHttpClient = OkHttpClient()
    private val moshi: Moshi = Moshi.Builder().build()

    @OptIn(ExperimentalStdlibApi::class)
    private val moshiAdapter = moshi.adapter<Map<String, *>>()

    init {
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

    fun getMatchDetails(matchId: Long) : DotaMatchDetails {
        return dotaClient.getMatchDetails(matchId, timeoutInMillis).get()
    }

    fun getProfileCard(accountId: Int) : DotaProfileCard {
        return dotaClient.getAccountProfileCard(accountId, timeoutInMillis).get()
    }

    fun getPlayerStats(accountId: Int) : DotaPlayerStats {
        return getPlayerStatsRequest(accountId, timeoutInMillis).get()
    }

    fun getPlayerDetails(accountId: Int) {
        TODO("Not yet implemented")
    }

    fun getPlayerMatchHistory(accountId: Int) {
        TODO("Not yes implemented")
    }

    fun sendToWebAPI(interfaceName: String, methodName: String, version: String, args: Map<String, *>) : Map<String, *>? {
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
}