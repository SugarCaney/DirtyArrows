package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.applyBowEnchantments
import nl.sugcube.dirtyarrows.util.copyOf
import nl.sugcube.dirtyarrows.util.fuzz
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.AbstractArrow
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack

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
        costRequirements = listOf(ItemStack(Material.ARROW, 8)),
        description = "Shoots multiple arrows simultaneously."
) {

    /**
     * The amount of arrows to fire at once, must be at least 1.
     */
    val arrowCount = config.getInt("$node.arrow-count")

    /**
     * The maximum deviation of the arrows from the base arrow along each axis
     */
    val directionFuzz = config.getDouble("$node.direction-fuzz")

    init {
        check(arrowCount >= 1) { "$node.arrow-count must be greater than or equal to 1, got <$arrowCount>" }
    }

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        // Launch 1 extra arrows.
        repeat(arrowCount - 1) {
            player.world.spawn(arrow.location, Arrow::class.java).apply {
                shooter = player
                velocity = arrow.velocity.copyOf(
                        x = arrow.velocity.x.fuzz(directionFuzz),
                        y = arrow.velocity.y.fuzz(directionFuzz),
                        z = arrow.velocity.z.fuzz(directionFuzz)
                )
                applyBowEnchantments(player.bowItem())
                isCritical = true

                if (player.gameMode == GameMode.CREATIVE) {
                    pickupStatus = AbstractArrow.PickupStatus.CREATIVE_ONLY
                }
            }
        }
        unregisterArrow(arrow)
    }

    override fun Player.consumeBowItems() {
        if (gameMode == GameMode.CREATIVE) return

        val hasInfinity = bowItem()?.enchantments?.containsKey(Enchantment.ARROW_INFINITE) == true
        if (hasInfinity.not()) {
            inventory.removeItem(ItemStack(Material.ARROW, arrowCount))
        }
    }
}