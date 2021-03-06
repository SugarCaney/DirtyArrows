package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.applyBowEnchantments
import nl.sugcube.dirtyarrows.util.copyOf
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack
import kotlin.random.Random

/**
 * Shoots 12 arrows simultaneously in slightly different directions.
 *
 * @author SugarCaney
 */
open class MultiBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.MULTI,
        canShootInProtectedRegions = true,
        removeArrow = false,
        costRequirements = listOf(ItemStack(Material.ARROW, 12)),
        description = "Shoots multiple arrows simultaneously."
) {

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        // Launch 1 extra arrows.
        repeat(11) {
            player.world.spawn(arrow.location, Arrow::class.java).apply {
                shooter = player
                velocity = arrow.velocity.copyOf(
                        x = arrow.velocity.x + Random.nextDouble() / 2.0 - 0.254,
                        y = arrow.velocity.y + Random.nextDouble() / 2.0 - 0.254,
                        z = arrow.velocity.z + Random.nextDouble() / 2.0 - 0.254
                )
                isCritical = true
                applyBowEnchantments(player.bowItem())

                if (player.gameMode == GameMode.CREATIVE) {
                    pickupStatus = Arrow.PickupStatus.CREATIVE_ONLY
                }
            }
        }
        unregisterArrow(arrow)
    }

    override fun Player.consumeBowItems() {
        if (gameMode == GameMode.CREATIVE) return

        val hasInfinity = bowItem()?.enchantments?.containsKey(Enchantment.ARROW_INFINITE) == true
        if (hasInfinity.not()) {
            // Only subtract 7 instead of 8, because 1 gets fired by default.
            inventory.removeItem(ItemStack(Material.ARROW, 7))
        }
    }
}