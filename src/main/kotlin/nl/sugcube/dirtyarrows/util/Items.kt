package nl.sugcube.dirtyarrows.util

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.Damageable
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

    // Beware, this is org.bukkit.inventory.meta.Damageable, not org.bukkit.entity.Damageable
    val damageable = itemMeta ?: return
    if (damageable !is Damageable) return

    val unbreakingLevel = getEnchantmentLevel(Enchantment.DURABILITY)
    val reductionChance = 1.0 / (unbreakingLevel.toDouble() + 1.0)
    if (Random.nextDouble() < reductionChance) {
        damageable.damage = damageable.damage + 1
    }

    if (damageable.damage >= type.maxDurability) {
        amount = 0
        player.playSound(player.location, Sound.ENTITY_ITEM_BREAK, 10f, 1f)
    }

    itemMeta = damageable
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

/**
 * Checks if the inventory has (in total, so with all stacks combined) enough of items that match the
 * itemStack's material data and material type.
 */
fun Inventory.containsAtLeastInlcudingData(toCheck: ItemStack): Boolean {
    var count = 0
    forEachNotNull {
        if (it.type == toCheck.type && it.itemMeta == toCheck.itemMeta) {
             count += it.amount
        }
        if (count >= toCheck.amount) {
            return true
        }
    }
    return false
}

/**
 * Removes the given items from the inventory, also checks for material data.
 * The amount removed is the amount in the ItemStack.
 * It will combine all available stacks.
 */
fun Inventory.removeIncludingData(toRemove: ItemStack) {
    // The total amount of items that must be removed from the inventory.
    val totalToRemove = toRemove.amount

    // The amount of items that have been removed until 'now'.
    var amountRemoved = 0

    // Use a regular loop, to prevent modifying the inventory while iterating.
    for (i in 0 until size) {
        // Check if the item is eligible to be removed.
        val item = getItem(i) ?: continue
        if (item.type != toRemove.type || item.itemMeta != toRemove.itemMeta) continue

        // The amount of items that are yet to be removed.
        val targetToRemove = totalToRemove - amountRemoved

        // All items that need to be removed are removed.
        if (targetToRemove < item.amount) {
            val newAmount = item.amount - targetToRemove
            val newItem = ItemStack(item.type, newAmount)
            newItem.itemMeta = item.itemMeta
            setItem(i, newItem)
            return
        }

        // Removal threshold not met yet.
        amountRemoved = item.amount
        setItem(i, ItemStack(Material.AIR, 0))
    }
}