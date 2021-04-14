package nl.sugcube.dirtyarrows.effect

import nl.sugcube.dirtyarrows.DirtyArrows
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import kotlin.random.Random

/**
 * Makes it possible to get looting on a bow using the enchanting table.
 * - Available from level 11 and higher.
 * - 0--10 No looting
 * - 11--19 is Looting I
 * - 20--28 is Looting II
 * - 28+ is Looting III
 * - On all levels has a 12.5% chance of being applied by default.
 * - Has a 35% chance of being the only enchantment by default.
 * All numbers are configurable.
 *
 * No need to add custom functionality of dropping more items as that happens by default when holding
 * the looting bow.
 *
 * @author SugarCaney
 */
open class LootingOnBow(private val plugin: DirtyArrows) : Listener {

    /**
     * The chance of Looting to appear on the bow in range [0,1].
     * A value of 0 means that it's disabled.
     */
    val appearanceChance: Double
        get() = plugin.config.getDouble("looting.appearance-chance")

    /**
     * The chance that Looting is the only enchantment when enchanting in range [0,1].
     */
    val onlyLootingChance: Double
        get() = plugin.config.getDouble("looting.only-looting-chance")

    /**
     * The minimum amount of levels required to get a level 1 looting enchantment.
     */
    val minimumExperienceLevel1: Int
        get() = plugin.config.getInt("looting.minimum-xp-level-1")

    /**
     * The minimum amount of levels required to get a level 2 looting enchantment.
     */
    val minimumExperienceLevel2: Int
        get() = plugin.config.getInt("looting.minimum-xp-level-2")

    /**
     * The minimum amount of levels required to get a level 3 looting enchantment.
     */
    val minimumExperienceLevel3: Int
        get() = plugin.config.getInt("looting.minimum-xp-level-3")

    init {
        check(appearanceChance in 0.0..1.0) { "looting.appearance-chance must be between 0 and 1, got <$appearanceChance>" }
        check(onlyLootingChance in 0.0..1.0) { "looting.only-looting-chance must be between 0 and 1, got <$onlyLootingChance>" }
    }

    @EventHandler
    fun enchantBow(event: EnchantItemEvent) {
        val item = event.item
        if (item.type != Material.BOW) return
        if (Random.nextDouble() >= appearanceChance) return

        val cost = event.expLevelCost
        val level = when {
            cost < minimumExperienceLevel1 -> return
            cost < minimumExperienceLevel2 -> 1
            cost < minimumExperienceLevel3 -> 2
            else -> 3
        }

        val toAdd = event.enchantsToAdd
        if (Random.nextDouble() < onlyLootingChance) {
            toAdd.clear()
        }
        toAdd[Enchantment.LOOT_BONUS_MOBS] = level
    }
}