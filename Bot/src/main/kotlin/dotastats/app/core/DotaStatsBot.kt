package dotastats.app.core

import com.jagrosh.jdautilities.command.CommandClientBuilder
import dotastats.SteamConnect.SteamConnect
import dotastats.app.commands.GetMatchDetails
import dotastats.app.commands.GetUserStats
import io.github.cdimascio.dotenv.Dotenv
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.dv8tion.jda.api.entities.Activity
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.requests.GatewayIntent
import net.dv8tion.jda.api.utils.cache.CacheFlag

class DotaStatsBot() {
    init {
        val commandClientBuilder = CommandClientBuilder()
            .setStatus(OnlineStatus.ONLINE)
            .setActivity(Activity.customStatus("In development..."))
            .useHelpBuilder(false)
            .setOwnerId(dotenv["DISCORD_OWNER_ID"])

        commandClientBuilder.addSlashCommands(GetMatchDetails())
        commandClientBuilder.addSlashCommands(GetUserStats())

        JDABuilder.create(dotenv["DISCORD_TOKEN"], GatewayIntent.getIntents(GatewayIntent.DEFAULT))
            .disableCache(CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.ONLINE_STATUS)
            .addEventListeners(commandClientBuilder.build())
            .build()
        Message.suppressContentIntentWarning()
    }
    companion object {
        private val dotenv: Dotenv = Dotenv.load()
        val steamConnect: SteamConnect = SteamConnect(dotenv["STEAM_LOGIN"], dotenv["STEAM_PASSWORD"], dotenv["STEAM_WEB_API_KEY"])

        val color = 0xb0444f
    }
}