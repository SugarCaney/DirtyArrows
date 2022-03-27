package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.itemInOffHand
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

/**
 * Acts as a totem of undying - saves you from death when held.
 *
 * @author SugarCaney
 */
open class UndyingBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.UNDYING,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "Saves you from death when held."
) {

    @EventHandler
    fun saveFromDeath(event: PlayerDeathEvent) {
        val player = event.entity
        val bow = player.bowItem() ?: return

        // Temporarily give a totem of undying.
        val cachedOffHand = player.itemInOffHand
        val originalDamageCause = player.lastDamageCause

        with(player) {
            health = 0.5
            inventory.setItemInOffHand(ItemStack(Material.TOTEM_OF_UNDYING, 1))
            fireTicks = 0
            damage(999999.9)
            lastDamageCause = originalDamageCause
            event.deathMessage = null
            addPotionEffect(PotionEffect(PotionEffectType.FIRE_RESISTANCE, 200, 0))
            addPotionEffect(PotionEffect(PotionEffectType.WATER_BREATHING, 200, 0))
        }

        // Completely use up the bow. If it's in the offhand, removeItem doesnt work for some reason.
        if (cachedOffHand != bow) {
            player.inventory.setItemInOffHand(cachedOffHand)
            player.inventory.removeItem(bow)
        }
    }
}