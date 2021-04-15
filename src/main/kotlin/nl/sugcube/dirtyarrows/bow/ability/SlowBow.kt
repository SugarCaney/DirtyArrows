package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.util.Vector

/**
 * Shoots a very, very slow arrow that One hit KO's everything.
 *
 * @author SugarCaney
 */
open class SlowBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.SLOW,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "Shoots an extremely powerful, but extremely slow arrow."
) {

    /**
     * Stores all velocities of the shot arrows, and their starting unix timestamp.
     */
    private val velocityAndAge = HashMap<Arrow, Pair<Vector, Long>>()

    /**
     * After what time the arrows should despawn, in milliseconds.
     */
    val despawnTime = config.getInt("$node.despawn-time")

    /**
     * How much damage a slow arrow does.
     */
    val damage = config.getDouble("$node.damage")

    /**
     * With what number to multiply the default arrow speed.
     */
    val arrowSpeed = config.getDouble("$node.arrow-speed")

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        arrow.velocity = arrow.velocity.multiply(arrowSpeed)
        velocityAndAge[arrow] = arrow.velocity to System.currentTimeMillis()
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        velocityAndAge.remove(arrow)
        monsterDamage(event.hitEntity, player)
    }

    /**
     * Kills the given entity.
     */
    fun monsterDamage(target: Entity?, player: Player) {
        val livingEntity = target as? LivingEntity
        livingEntity?.damage(damage, player)
    }

    override fun effect() {
        arrows.forEach { arrow ->
            val (velocity, _) = velocityAndAge[arrow]!!
            arrow.velocity = velocity
        }

        velocityAndAge.keys.removeIf {
            // Despawn old arrows and remove them from the tracker maps, as they won't disappear from the Set and Maps
            // automatically.
            val (_, birthTime) = velocityAndAge[it]!!
            val age = System.currentTimeMillis() - birthTime
            if (age >= despawnTime) {
                unregisterArrow(it)
                true
            }
            else false
        }
    }
}