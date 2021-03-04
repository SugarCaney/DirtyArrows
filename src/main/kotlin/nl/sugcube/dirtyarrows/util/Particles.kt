package nl.sugcube.dirtyarrows.util

import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.entity.Entity
import org.bukkit.potion.PotionType

/**
 * Shows a smoke particle on the location of the entity.
 */
fun Entity.showSmokeParticle() = location.clone().add(0.5, 0.5, 0.5).let {
    location.world.playEffect(it, Effect.SMOKE, 0)
}

/**
 * Shows a smoke particle on this location.
 */
fun Location.showSmokeParticle() = clone().add(0.5, 0.5, 0.5).let {
    world.playEffect(it, Effect.SMOKE, 0)
}

/**
 * Shows a mobspawner flame particle on the location of the entity.
 */
fun Entity.showFlameParticle() = location.clone().add(0.5, 0.5, 0.5).let {
    location.world.playEffect(it, Effect.MOBSPAWNER_FLAMES, 0)
}

/**
 * Shows a mobspawner flame particle on the location of the entity.
 */
fun Location.showFlameParticle() = clone().add(0.5, 0.5, 0.5).let {
    world.playEffect(it, Effect.MOBSPAWNER_FLAMES, 0)
}

/**
 * Shows a potion break particle at the given location.
 */
fun Location.showPotionParticle(potionType: PotionType) = let {
    val color = potionType.effectType.color
    repeat(50) { _ ->
        val location = it.fuzz(0.5)
        world.spawnParticle(
                Particle.REDSTONE,
                location.x, location.y + 1.0, location.z,
                0,
                color.red / 255.0, color.green / 255.0, color.blue / 255.0
        )
    }

}

/**
 * Shows an ender particle at the location of the entity.
 */
fun Entity.showEnderParticle() {
    location.world.playEffect(location, Effect.ENDER_SIGNAL, 0)
}

/**
 * Shows a growth particle at the location.
 *
 * @param strength
 *          The amount of particles to show.
 */
fun Location.showGrowthParticle(strength: Int = 5) {
    world.playEffect(this, Effect.VILLAGER_PLANT_GROW, strength)
}