package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.launchArrowAt
import nl.sugcube.dirtyarrows.util.nearbyLivingEntities
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent

/**
 * Arrows that hit entities get redirected to the closest other entity.
 *
 * @author SugarCaney
 */
open class ChainBow(plugin: DirtyArrows) : BowAbility(
    plugin = plugin,
    type = DefaultBow.CHAIN,
    canShootInProtectedRegions = false,
    removeArrow = false,
    description = "Arrows ricochet to the closest entity after hitting a target."
) {

    /**
     * How far away to look for entities to chain toward.
     */
    val searchRange = config.getDouble("$node.search-range")

    /**
     * By how much to multiply the velocity of the arrow after each bounce.
     */
    val velocityMultiplier = config.getDouble("$node.velocity-multiplier")

    /**
     * Maps each arrow to all targets it has already hit.
     */
    private val hasBeenHit = HashMap<Arrow, HashSet<LivingEntity>>()

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        // Make sure not to ricochet back to the shooter.
        hasBeenHit[arrow] = hashSetOf(player)
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val entity = event.hitEntity as? LivingEntity ?: return

        val alreadyTargeted = hasBeenHit[arrow] ?: HashSet()
        alreadyTargeted.add(entity)

        val possibleTargets = entity.location.nearbyLivingEntities(searchRange)
            .filter { it !in alreadyTargeted }
            .sortedBy { it.location.distance(arrow.location) }

        // When there are no valid entities: stop ricocheting
        hasBeenHit.remove(arrow)
        val newTarget = possibleTargets.firstOrNull() ?: return
        alreadyTargeted.add(newTarget)

        val newArrow = entity.location.clone()
            .add(0.0, 1.0, 0.0)
            .launchArrowAt(newTarget.location, arrow.velocity.length() * velocityMultiplier, player, player.bowItem()) ?: return
        registerArrow(newArrow)

        hasBeenHit[newArrow] = alreadyTargeted
    }
}
