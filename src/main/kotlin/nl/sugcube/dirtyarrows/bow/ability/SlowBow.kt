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
        removeArrow = false
) {

    /**
     * Stores all velocities of the shot arrows, and their starting unix timestamp.
     */
    private val velocityAndAge = HashMap<Arrow, Pair<Vector, Long>>()

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        arrow.velocity = arrow.velocity.multiply(0.12)
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
        livingEntity?.damage(99999.0, player)
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
            if (age >= DESPAWN_TIME) {
                unregisterArrow(it)
                true
            }
            else false
        }
    }

    companion object {

        /**
         * In what time the arrows should despawn.
         */
        const val DESPAWN_TIME = 59000
    }
}