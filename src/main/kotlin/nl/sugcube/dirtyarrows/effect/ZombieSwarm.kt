@file:JvmName("ZombieSwarm")

package nl.sugcube.dirtyarrows.effect

import nl.sugcube.dirtyarrows.util.firstSpawnEligibleY
import nl.sugcube.dirtyarrows.util.spawn
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.entity.Zombie
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Spawns a circle of zombies around the given location.
 *
 * @param zombieCount
 *          The amount of zombies to spawn (strictly greater than 0).
 * @param distance
 *          How far from the location the zombies must spawn (radius, >= 0).
 */
fun Location.spawnZombieSwarm(zombieCount: Int = 16, distance: Double = 5.0) {
    require(zombieCount > 0) { "Zombie count must be positive, was $zombieCount" }
    require(distance >= 0.0) { "Distance must be nonnegative, was $distance" }

    for (i in 0 until zombieCount) {
        val angle = 2 * PI * i / zombieCount.toDouble()
        val circleX = x + distance * cos(angle)
        val circleZ = z + distance * sin(angle)

        Location(world, circleX, y, circleZ).let {
            it.y = it.firstSpawnEligibleY()?.toDouble() ?: y
            it.spawn(Zombie::class)
            world.playEffect(it, Effect.MOBSPAWNER_FLAMES, 0)
        }
    }
}