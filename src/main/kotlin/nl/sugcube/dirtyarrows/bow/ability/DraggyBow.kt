package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.copyOf
import nl.sugcube.dirtyarrows.util.diameter
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent

/**
 * Drags all close entities to the landing position.
 * Effect stops when hitting an entity, so you have to aim around the target.
 *
 * @author SugarCaney
 */
open class DraggyBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.DRAGGY,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = false,
        removeArrow = false,
        description = "Arrows drag close entities to the landing spot."
) {

    /**
     * Maps all draggy arrows to their affected entities and starting unix timestamp.
     */
    private val arrowAge = HashMap<Arrow, Pair<MutableSet<LivingEntity>, Long>>()

    /**
     * Maps each entity to the arrow they are affected by.
     */
    private val draggedEntities = HashMap<LivingEntity, Arrow>()

    /**
     * Maximum time the drag ability can be applied. In milliseconds.
     */
    val maximumLifespanMillis = config.getInt("$node.max-lifespan")

    /**
     * How far from the arrows entities can get dragged, in blocks.
     */
    val effectRange = config.getDouble("$node.effect-range")

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        arrowAge[arrow] = HashSet<LivingEntity>() to System.currentTimeMillis()
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        arrow.reset()
    }

    /**
     * Unregisters this arrow from the Draggy ability.
     */
    private fun Arrow.reset() {
        val (entities, _) = arrowAge[this] ?: return
        entities.forEach { draggedEntities.remove(it) }
        arrowAge.remove(this)
    }

    override fun effect() {
        registerNewTargets()
        moveTargets()
        removeExpiredArrows()
    }

    /**
     * Removes the drag effect from arrows that exceeded their lifespan [maximumLifespanMillis].
     */
    private fun removeExpiredArrows() = arrowAge.entries.removeIf { (_, entitiesBirthTimePair) ->
        val (targets, birthTime) = entitiesBirthTimePair

        if (System.currentTimeMillis() - birthTime >= maximumLifespanMillis) {
            targets.forEach { draggedEntities.remove(it) }
            true
        }
        else false
    }

    /**
     * Teleports all targets the arrow the're dragged by.
     */
    private fun moveTargets() = draggedEntities.entries.forEach { (target, arrow) ->
        val targetLocation = target.location
        val displacement = target.diameter + 0.25
        val teleportLocation = arrow.location.subtract(arrow.velocity.normalize().multiply(displacement))
        target.teleport(teleportLocation.copyOf(
                yaw = targetLocation.yaw,
                pitch = targetLocation.pitch
        ))
    }

    /**
     * Looks for new targets to drag around the registered arrows.
     */
    private fun registerNewTargets() = arrows.forEach { arrow ->
        arrow.getNearbyEntities(effectRange, effectRange, effectRange).asSequence()
                .mapNotNull { it as? LivingEntity }
                .filter { arrow.location.distance(it.location) <= effectRange }
                .filter { it != arrow.shooter }
                .filter { it !in draggedEntities }
                .forEach {
                    arrowAge[arrow]?.first?.add(it)
                    draggedEntities[it] = arrow
                }
    }
}