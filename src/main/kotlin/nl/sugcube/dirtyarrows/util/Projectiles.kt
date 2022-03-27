package nl.sugcube.dirtyarrows.util

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

/**
 * Applies effects for the bow enchantments to this arrow.
 */
fun Arrow.applyBowEnchantments(bow: ItemStack?) {
    // When these are inifinity arrows, they cannot be picked up.
    if (bow?.containsEnchantment(Enchantment.ARROW_INFINITE) == false) {
        pickupStatus = Arrow.PickupStatus.ALLOWED
    }

    // Set punch enchantment.
    knockbackStrength = bow?.getEnchantmentLevel(Enchantment.ARROW_KNOCKBACK) ?: 0

    // Make them critcal if the bow has power.
    // Does not fully account for all levels, but I wouldn't bother too much. This is fine.
    isCritical = bow?.containsEnchantment(Enchantment.ARROW_DAMAGE) == true

    // Set on fire when the bow has flame.
    if (bow?.containsEnchantment(Enchantment.ARROW_FIRE) == true) {
        fireTicks = 1200
    }
}

/**
 * Gives back N amount of arrows if the bow has infinity.
 */
fun Inventory.returnInfinityArrows(bow: ItemStack?, arrows: Int) {
    val hasInfinity = bow?.enchantments?.containsKey(Enchantment.ARROW_INFINITE) == true
    if (hasInfinity.not()) {
        removeItem(ItemStack(Material.ARROW, arrows))
    }
}

/**
 * Checks if the player has arrows in their inventory.
 */
fun Player.hasArrows() = gameMode == GameMode.CREATIVE || inventory.contains(Material.ARROW)

/**
 * Removes an arrow from the player's inventory if applicable.
 * Accounts for Infinity and durability.
 *
 * @param this
 *          The player to remove the arrow from.
 * @param bowItem
 *          The bow item used to fire the arrow.
 */
fun Player.removeArrow(bowItem: ItemStack?) {
    bowItem?.subtractDurability(player ?: return)
    if (bowItem?.containsEnchantment(Enchantment.ARROW_INFINITE) == false) {
        player?.inventory?.removeItem(ItemStack(Material.ARROW, 1))
    }
}