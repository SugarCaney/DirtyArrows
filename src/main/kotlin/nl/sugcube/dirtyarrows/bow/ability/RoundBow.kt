package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.applyBowEnchantments
import nl.sugcube.dirtyarrows.util.copyOf
import nl.sugcube.dirtyarrows.util.rotateAlongYAxis
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.PI

/**
 * Shoots 30 arrows around the player.
 *
 * @author SugarCaney
 */
open class RoundBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.ROUND,
        canShootInProtectedRegions = true,
        removeArrow = false,
        costRequirements = listOf(ItemStack(Material.ARROW, 1)),
        description = "Shoots arrows all around you."
) {

    /**
     * The amount of arrows that get fired.
     */
    val arrowCount = config.getInt("$node.arrow-count")

    init {
        check(arrowCount >= 0) { "$node.arrow-count cannot be negative, got <$arrowCount>" }
    }

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        // First arrow is already shot by default.
        repeat(arrowCount - 1) {
            if (player.checkHasArrow().not()) return

            val initialDirection = arrow.velocity.copyOf().normalize()
            val angle = 2.0 * PI * (it + 1.0) / arrowCount.toDouble()
            val direction = initialDirection.rotateAlongYAxis(angle)
            val initialVelocity = direction.multiply(arrow.velocity.length())

            player.world.spawn(arrow.location, Arrow::class.java).apply {
                shooter = player
                velocity = initialVelocity
                applyBowEnchantments(player.bowItem())

                if (player.gameMode == GameMode.CREATIVE) {
                    pickupStatus = Arrow.PickupStatus.CREATIVE_ONLY
                }
            }
        }
        unregisterArrow(arrow)
    }

    /**
     * Checks if the player has an arrow available, and removes it if it is.
     *
     * @return `true` if there is an arrow available, `false` if not.
     */
    private fun Player.checkHasArrow(): Boolean {
        if (gameMode == GameMode.CREATIVE) return true
        val bow = bowItem() ?: return false
        if (bow.containsEnchantment(Enchantment.ARROW_INFINITE)) return true

        if (inventory.contains(Material.ARROW, 1)) {
            inventory.removeItem(ItemStack(Material.ARROW, 1))
            return true
        }

        return false
    }

    override fun Player.consumeBowItems() {
        if (gameMode == GameMode.CREATIVE) return
    }
}