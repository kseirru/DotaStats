package dotastats.SteamConnect.Handlers

import com.avenga.steamclient.base.ClientGCProtobufMessage
import com.avenga.steamclient.base.GCPacketMessage
import com.avenga.steamclient.model.steam.SteamMessageCallback
import com.avenga.steamclient.protobufs.dota.DotaGCMessagesClient
import com.avenga.steamclient.steam.client.SteamClient
import com.avenga.steamclient.steam.client.callback.AbstractCallbackHandler
import com.avenga.steamclient.util.CallbackHandlerUtils
import java.util.*

class PlayerMatchHistoryHandler : AbstractCallbackHandler<GCPacketMessage>() {
    companion object {
        fun handle(callback: SteamMessageCallback<GCPacketMessage>, timeout: Long, client: SteamClient) : Optional<MutableList<DotaGCMessagesClient.CMsgDOTAGetPlayerMatchHistoryResponse.Match>?>? {
            val gcPacketMessage: Optional<GCPacketMessage> = waitAndGetMessageOrRemoveAfterTimeout(callback, timeout, "PlayerMatchHistory", client)
            return CallbackHandlerUtils.getValueOrDefault(gcPacketMessage, PlayerMatchHistoryHandler.Companion::getMessage)
        }

        fun getMessage(gcPacketMessage: GCPacketMessage) : MutableList<DotaGCMessagesClient.CMsgDOTAGetPlayerMatchHistoryResponse.Match>? {
            val protobufMessage: ClientGCProtobufMessage<DotaGCMessagesClient.CMsgGCToClientPlayerStatsResponse.Builder> = ClientGCProtobufMessage(
                DotaGCMessagesClient.CMsgGCToClientPlayerStatsResponse::class.java, gcPacketMessage)
            return (protobufMessage.body as DotaGCMessagesClient.CMsgDOTAGetPlayerMatchHistoryResponse.Builder).matchesList
        }
    }
}