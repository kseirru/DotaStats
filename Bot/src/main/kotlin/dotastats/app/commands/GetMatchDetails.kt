package dotastats.app.commands

import com.jagrosh.jdautilities.command.SlashCommand
import com.jagrosh.jdautilities.command.SlashCommandEvent
import dotastats.app.core.DotaStatsBot
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class GetMatchDetails : SlashCommand() {
    init {
        name = "match"
        help = "Get match details"

        options = listOf(
            OptionData(OptionType.STRING, "matchid", "Match ID", true, false)
        )

        guildOnly = true
    }

    override fun execute(event: SlashCommandEvent?) {
        val matchDetails = DotaStatsBot.steamConnect.getMatchDetails((event?.getOption("matchid")?.asString)!!.toLong()) // TODO: Make a normal response & options
        event.reply("iDK duration\n" +
                "${matchDetails.result} result").queue()
    }
}