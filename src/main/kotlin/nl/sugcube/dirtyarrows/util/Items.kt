package nl.sugcube.dirtyarrows.util

import org.bukkit.GameMode
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

/**
 * Removes durability from the given item and removes it if it has no more durability.
 * Does consider Unbreaking enchantments and gamemode.
 *
 * @param player
 *          Owner of the item.
 */
fun ItemStack.subtractDurability(player: Player) {
    if (player.gameMode == GameMode.CREATIVE) return

    val unbreakingLevel = getEnchantmentLevel(Enchantment.DURABILITY)
    val reductionChance = 1.0 / (unbreakingLevel.toDouble() + 1.0)
    if (Random.nextDouble() < reductionChance) {
        durability = (durability + 1).toShort()
    }

    if (durability >= type.maxDurability) {
        amount = 0

        player.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 10f, 1f)
    }
}

/**
 * Get the maximum tool level available.
 */
fun Player.maxToolLevel(): ToolLevel = inventory.asSequence()
        .filter { it?.type in BLOCK_BREAK_TOOLS }
        .mapNotNull { it?.type?.toolLevel }
        .maxByOrNull { it }
        ?: ToolLevel.BARE_HANDS

/**
 * Checks if the player has a silk touch pickaxe in their inventory.
 */
fun Player.hasSilkTouchOnPickaxe() = inventory.asSequence()
        .filter { it?.type in ToolLevel.PICKAXES }
        .any { it.containsEnchantment(Enchantment.SILK_TOUCH) }

/**
 * Get the maximum level of fortune in the player's inventory.
 */
fun Player.fortuneLevel() = inventory.maxOf { it?.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) ?: 0 }