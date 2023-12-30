package dotastats.SteamConnect.models

import net.platinumdigitalgroup.jvdf.VDFNode
import kotlin.math.roundToInt

class DotaHero(data: VDFNode, key: String, localizedName: String) {
    val heroName: String = localizedName
    val heroKey: String = key
    val isEnabled: Boolean = data.getInt("Enabled") == 1
    val heroId: Int = data.getInt("HeroID")
    val availableForNewPlayers: Boolean = data.getInt("new_player_enable") == 1
    val abilitiesKeys: MutableList<String> = mutableListOf()
    val baseArmor: Float = data.getFloat("ArmorPhysical")
    val statusHealthRegen: Float = data.getFloat("StatusHealthRegen")
    val attackDamageMin: Int = data.getInt("AttackDamageMin")
    val attackDamageMax: Int = data.getInt("AttackDamageMax")
    val attackRate: Float = data.getFloat("AttackRate")
    val attackRange: Int = data.getInt("AttackRange")
    val attributePrimary: DotaAttribute = when (data.getString("AttributePrimary")) {
        "DOTA_ATTRIBUTE_STRENGTH" -> {
            DotaAttribute.STRENGTH
        }
        "DOTA_ATTRIBUTE_AGILITY" -> {
            DotaAttribute.AGILITY
        }
        "DOTA_ATTRIBUTE_INTELLIGENCE" -> {
            DotaAttribute.INTELLIGENCE
        }
        else -> {
            DotaAttribute.ALL
        }
    }

    private val baseHealth: Int = 120
    private val baseMana: Int = 75

    val attributeBaseStrength: Int = data.getInt("AttributeBaseStrength")
    val attributeBaseAgility: Int = data.getInt("AttributeBaseAgility")
    val attributeBaseIntelligence: Int = data.getInt("AttributeBaseIntelligence")

    val attributeStrengthGain: Float = data.getFloat("AttributeStrengthGain")
    val attributeAgilityGain: Float = data.getFloat("AttributeAgilityGain")
    val attributeIntelligenceGain: Float = data.getFloat("AttributeIntelligenceGain")

    val movementSpeed: Int = data.getInt("MovementSpeed")

    val health: Int = baseHealth + (22 * attributeBaseStrength)
    val mana: Int = baseMana + (12 * attributeBaseIntelligence)
    val armor: Int = (baseArmor + (0.167 * attributeBaseAgility)).roundToInt()

    private val heroNameFromKey: String = heroKey.split("npc_dota_hero_")[1]

    val dotaPage: String = "https://www.dota2.com/hero/$heroNameFromKey"
    val modelRenderImage: String = "https://cdn.cloudflare.steamstatic.com/apps/dota2/videos/dota_react/heroes/renders/$heroNameFromKey.png"
    val modelRenderVideo: String = "https://cdn.cloudflare.steamstatic.com/apps/dota2/videos/dota_react/heroes/renders/$heroNameFromKey.webm"
    val heroImage: String = "https://cdn.dota2.com/apps/dota2/images/heroes/${heroNameFromKey}_full.png"
    init {
        var errorCatcher = true
        var abilityNumber = 1
        while (errorCatcher) {
            try {
                abilitiesKeys.add(data.getString("Ability$abilityNumber"))
                abilityNumber++
            } catch (e: Exception) {
                errorCatcher = false
            }
        }
    }
}