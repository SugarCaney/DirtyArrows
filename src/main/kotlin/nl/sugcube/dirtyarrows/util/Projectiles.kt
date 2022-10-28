package nl.sugcube.dirtyarrows.util

import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.*
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector

/**
 * Applies effects for the bow enchantments to this arrow.
 */
fun Arrow.applyBowEnchantments(bow: ItemStack?) {
    // When these are inifinity arrows, they cannot be picked up.
    if (bow?.containsEnchantment(Enchantment.ARROW_INFINITE) == false) {
        pickupStatus = AbstractArrow.PickupStatus.ALLOWED
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

/**
 * Launches an arrow from `this` location to the `target` location.
 *
 * @param target
 *          The target location to shoot at.
 * @param velocity
 *          The speed (Vector length of the velocity) to shoot at.
 * @param shooter
 *          Who shoots the arrow, `null` for no shooter.
 * @param bow
 *          The bow to shoot the arrow with, `null` for no bow.
 */
fun Location.launchArrowAt(
        target: Location,
        velocity: Double,
        shooter: LivingEntity? = null,
        bow: ItemStack? = null
) : Arrow? {
    val direction = target.copyOf().subtract(this)
    val arrowVelocity = direction.toVector().normalize().multiply(velocity).add(Vector(0.0, 0.25, 0.0))

    return world?.spawnEntity(this, EntityType.ARROW)?.apply {
        val arrow = this as Arrow
        arrow.shooter = shooter
        arrow.velocity = arrowVelocity
        arrow.applyBowEnchantments(bow)

        if (shooter is Player && shooter.gameMode == GameMode.CREATIVE) {
            arrow.pickupStatus = AbstractArrow.PickupStatus.CREATIVE_ONLY
        }
    } as? Arrow
}