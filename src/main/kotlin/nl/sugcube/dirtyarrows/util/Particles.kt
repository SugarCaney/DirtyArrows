package nl.sugcube.dirtyarrows.util

import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.entity.Entity

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
 * Shows an ender particle at the location of the entity.
 */
fun Entity.showEnderParticle() {
    location.world.playEffect(location, Effect.ENDER_SIGNAL, 0)
}

/**
 * Shows a growth particle at the location.
 */
fun Location.showGrowthParticle() {
    world.playEffect(this, Effect.VILLAGER_PLANT_GROW, 0)
}