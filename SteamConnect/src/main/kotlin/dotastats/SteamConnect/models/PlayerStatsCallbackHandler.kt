package dotastats.SteamConnect.models

import com.avenga.steamclient.base.ClientGCProtobufMessage
import com.avenga.steamclient.base.GCPacketMessage
import com.avenga.steamclient.model.steam.SteamMessageCallback
import com.avenga.steamclient.protobufs.dota.DotaGCMessagesClient
import com.avenga.steamclient.steam.client.SteamClient
import com.avenga.steamclient.steam.client.callback.AbstractCallbackHandler
import com.avenga.steamclient.util.CallbackHandlerUtils
import java.util.*

class PlayerStatsCallbackHandler : AbstractCallbackHandler<GCPacketMessage>() {
    companion object {
        fun handle(callback: SteamMessageCallback<GCPacketMessage>, timeout: Long, client: SteamClient) : Optional<DotaPlayerStats> {
            val gcPacketMessage: Optional<GCPacketMessage> = waitAndGetMessageOrRemoveAfterTimeout(callback, timeout, "PlayerStats", client)
            return CallbackHandlerUtils.getValueOrDefault(gcPacketMessage, PlayerStatsCallbackHandler::getMessage)
        }

        fun getMessage(gcPacketMessage: GCPacketMessage) : DotaPlayerStats {
            val protobufMessage: ClientGCProtobufMessage<DotaGCMessagesClient.CMsgGCToClientPlayerStatsResponse.Builder> = ClientGCProtobufMessage(DotaGCMessagesClient.CMsgGCToClientPlayerStatsResponse::class.java, gcPacketMessage)
            return DotaPlayerStats(protobufMessage.body as DotaGCMessagesClient.CMsgGCToClientPlayerStatsResponse.Builder)
        }
    }
}