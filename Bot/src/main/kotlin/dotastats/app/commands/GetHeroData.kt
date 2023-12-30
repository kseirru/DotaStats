package dotastats.app.commands

import com.jagrosh.jdautilities.command.SlashCommand
import com.jagrosh.jdautilities.command.SlashCommandEvent
import dev.minn.jda.ktx.messages.Embed
import dotastats.app.core.DotaStatsBot
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.OptionData

class GetHeroData : SlashCommand() {
    init {
        name = "hero"
        help = "Get information about Hero"

        options = listOf(
            OptionData(OptionType.INTEGER, "heroid", "ID of hero", true, false)
        )
    }

    override fun execute(event: SlashCommandEvent) {
        val hero = DotaStatsBot.steamConnect.getHero(event.getOption("heroid")?.asInt!!)
        event.replyEmbeds(Embed {
            title = hero?.heroName
            color = DotaStatsBot.COLOR

            field {
                name = "Attributes"
                value = "**• Strength**: ${hero?.attributeBaseStrength} + ${hero?.attributeStrengthGain}\n" +
                        "**• Agility**: ${hero?.attributeBaseAgility} + ${hero?.attributeAgilityGain}\n" +
                        "**• Intelligence**: ${hero?.attributeBaseIntelligence} + ${hero?.attributeIntelligenceGain}\n"
                inline = false
            }

            field {
                name = "Stats"
                value = "**• Health**: ${hero?.health}\n" +
                        "**• Mana**: ${hero?.mana}\n" +
                        "**• Armor**: ${hero?.armor}\n"
                inline = false
            }

            thumbnail = hero?.heroImage
            image = hero?.modelRenderImage
        }).queue()
    }
}