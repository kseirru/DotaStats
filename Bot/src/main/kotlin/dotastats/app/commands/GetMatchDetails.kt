package dotastats.app.commands

import com.jagrosh.jdautilities.command.SlashCommand
import com.jagrosh.jdautilities.command.SlashCommandEvent
import dev.minn.jda.ktx.messages.EmbedBuilder
import dotastats.app.core.DotaStatsBot
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class GetMatchDetails : SlashCommand() {
    init {
        name = "match"
        help = "Get match details"

        options = listOf(
            OptionData(OptionType.NUMBER, "matchid", "Match ID", true, false)
        )

        guildOnly = true
    }

    override fun execute(event: SlashCommandEvent) {
        val matchDetails = DotaStatsBot.steamConnect.getMatchDetails(event.getOption("matchid")?.asLong!!)
        val embed = EmbedBuilder {
            title = "Match: ${matchDetails.matchId}"
            color = DotaStatsBot.color
        }
    }
}