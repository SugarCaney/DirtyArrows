package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.Worlds
import nl.sugcube.dirtyarrows.util.nearbyLivingEntities
import nl.sugcube.dirtyarrows.util.scheduleRemoval
import nl.sugcube.dirtyarrows.util.showSmokeParticle
import org.bukkit.Location
import org.bukkit.entity.AbstractArrow.PickupStatus
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.util.Vector

/**
 * Shoots invisible arrows.
 *
 * @author SugarCaney
 */
open class InvisibilityBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.INVISIBILITY,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "Invisible arrows."
) {

    private val invisibleArrows = HashSet<InvisibleArrow>()

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        unregisterArrow(arrow)
        arrow.scheduleRemoval(plugin)

        invisibleArrows += InvisibleArrow(
            shooter = player,
            location = arrow.location.clone(),
            velocity = arrow.velocity.clone(),
            baseDamage = arrow.damage,
            pickupStatus = arrow.pickupStatus,
            knockbackStrength = arrow.knockbackStrength,
            fireTicks = arrow.fireTicks,
            critical = arrow.isCritical
        )
    }

    override fun effect() {
        invisibleArrows.forEach { it.tick() }

        invisibleArrows.forEach {
            // Project target.
            val projectedLocation = it.location.clone().add(it.velocity.clone().multiply(3.0))
            projectedLocation.nearbyLivingEntities(1.7).firstOrNull()?.let { _ ->
                it.appear()
            }

            // Hit the ground.
            if (it.location.block.type.isSolid) {
                it.appear()
            }
        }

        invisibleArrows.removeIf { it.isDone }
    }

    /**
     * @author SugarCaney
     */
    class InvisibleArrow(
        val shooter: LivingEntity,
        val location: Location,
        val velocity: Vector,
        val baseDamage: Double,
        val pickupStatus: PickupStatus,
        val knockbackStrength: Int,
        val fireTicks: Int,
        val critical: Boolean
    ) {

        var isDone = false

        fun tick() {
            velocity.y = velocity.y - Worlds.GRAVITY / 20.0

            location.add(velocity)
        }

        fun appear() {
            location.world!!.spawn(location.clone().subtract(velocity), Arrow::class.java).apply {
                shooter = this@InvisibleArrow.shooter
                velocity = this@InvisibleArrow.velocity
                damage = this@InvisibleArrow.baseDamage
                pickupStatus = this@InvisibleArrow.pickupStatus
                knockbackStrength = this@InvisibleArrow.knockbackStrength
                fireTicks = this@InvisibleArrow.fireTicks
                isCritical = this@InvisibleArrow.critical
            }
            location.showSmokeParticle()
            isDone = true
        }
    }
}