package nl.sugcube.dirtyarrows.util

import org.bukkit.Effect
import org.bukkit.entity.Entity

/**
 * Shows a smoke particle on the location of the entity.
 */
fun Entity.showSmokeParticle() = location.clone().add(0.5, 0.5, 0.5).let {
    location.world.playEffect(location, Effect.SMOKE, 0)
}

/**
 * Shows a mobspawner flame particle on the location of the entity.
 */
fun Entity.showFlameParticle() = location.clone().add(0.5, 0.5, 0.5).let {
    location.world.playEffect(location, Effect.MOBSPAWNER_FLAMES, 0)
}