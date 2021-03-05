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
 * - 11--17 is Looting I
 * - 18--24 is Looting II
 * - 25+ is Looting III
 * - On all levels has a 12.5% chance of being applied by default.
 * - Has a 35% chance of being the only enchantment by default.
 *
 * @author SugarCaney
 */
open class LootingOnBow(private val plugin: DirtyArrows) : Listener {

    @EventHandler
    fun enchantBow(event: EnchantItemEvent) {
        val item = event.item
        if (item.type != Material.BOW) return
        if (Random.nextDouble() >= APPEARANCE_CHANCE) return

        val cost = event.expLevelCost
        val level = when {
            cost < MINIMUM_EXPERIENCE_LEVEL_1 -> return
            cost < MINIMUM_EXPERIENCE_LEVEL_2 -> 1
            cost < MINIMUM_EXPERIENCE_LEVEL_3 -> 2
            else -> 3
        }

        val toAdd = event.enchantsToAdd
        if (Random.nextDouble() < ONLY_LOOTING_CHANCE) {
            toAdd.clear()
        }
        toAdd[Enchantment.LOOT_BONUS_MOBS] = level
    }

    companion object {

        /**
         * The chance of Looting to appear on the bow.
         */
        private const val APPEARANCE_CHANCE = 0.125

        /**
         * The chance that Looting is the only enchantment when enchanting.
         */
        private const val ONLY_LOOTING_CHANCE = 0.35

        /**
         * The minimum amount of levels required to get a level 1 looting enchantment.
         */
        private const val MINIMUM_EXPERIENCE_LEVEL_1 = 11

        /**
         * The minimum amount of levels required to get a level 2 looting enchantment.
         */
        private const val MINIMUM_EXPERIENCE_LEVEL_2 = 18

        /**
         * The minimum amount of levels required to get a level 3 looting enchantment.
         */
        private const val MINIMUM_EXPERIENCE_LEVEL_3 = 25
    }
}