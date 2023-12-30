package dotastats.app.commands

import com.jagrosh.jdautilities.command.SlashCommand
import com.jagrosh.jdautilities.command.SlashCommandEvent
import dev.minn.jda.ktx.messages.EmbedBuilder
import dotastats.app.core.DotaStatsBot
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class GetUserStats : SlashCommand() {
    init {
        name = "stats"
        help = "Get user stats"

        options = listOf(
            OptionData(OptionType.INTEGER, "accountid", "User account id", true, false)
        )

        guildOnly = true
    }

    override fun execute(event: SlashCommandEvent?) {
        val stats = DotaStatsBot.steamConnect.getPlayerStats(event?.getOption("accountid")?.asInt!!)
        val embed = EmbedBuilder {
            title = ""
        }
    }
}