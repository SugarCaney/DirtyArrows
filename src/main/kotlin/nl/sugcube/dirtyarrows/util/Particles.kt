package nl.sugcube.dirtyarrows.util

import org.bukkit.Color
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Particle.DustOptions
import org.bukkit.entity.Entity
import org.bukkit.potion.PotionType

/**
 * Shows a smoke particle on the location of the entity.
 */
fun Entity.showSmokeParticle() = location.clone().add(0.5, 0.5, 0.5).let {
    location.world?.playEffect(it, Effect.SMOKE, 0)
}

/**
 * Shows a smoke particle on this location.
 */
fun Location.showSmokeParticle() = clone().add(0.5, 0.5, 0.5).let {
    world?.playEffect(it, Effect.SMOKE, 0)
}

/**
 * Shows a mobspawner flame particle on the location of the entity.
 */
fun Entity.showFlameParticle() = location.clone().add(0.5, 0.5, 0.5).let {
    location.world?.playEffect(it, Effect.MOBSPAWNER_FLAMES, 0)
}

/**
 * Shows a mobspawner flame particle on the location of the entity.
 */
fun Location.showFlameParticle() = clone().add(0.5, 0.5, 0.5).let {
    world?.playEffect(it, Effect.MOBSPAWNER_FLAMES, 0)
}

/**
 * Shows a potion break particle at the given location.
 */
fun Location.showPotionParticle(potionType: PotionType) {
    fuzz(0.5)
            .add(0.0, 1.0, 0.0)
            .showColoredDust(potionType.effectType?.color ?: return, 50)
}

/**
 * Shows a coloured dust particle at the given location.
 *
 * @param color
 *          The colour of the dust particle.
 * @param count
 *          The amount of particles to show.
 */
fun Location.showColoredDust(color: Color, count: Int) = showColoredDust(color.red, color.green, color.blue, count)

/**
 * Shows a coloured dust particle at the given location.
 *
 * @param red
 *          The redness [0,255] of the particle.
 * @param green
 *          The greenness [0,255] of the particle.
 * @param blue
 *          The blueness [0,255] of the particle.
 * @param count
 *          The amount of particles to show.
 */
fun Location.showColoredDust(red: Int, green: Int, blue: Int, count: Int) = repeat(count) { _ ->
    val options = DustOptions(Color.fromRGB(red, green, blue), 1.0f)
    world?.spawnParticle(Particle.REDSTONE, this, 0, options)
}

/**
 * Shows an ender particle at the location of the entity.
 */
fun Entity.showEnderParticle() {
    location.world?.playEffect(location, Effect.ENDER_SIGNAL, 0)
}

/**
 * Shows a growth particle at the location.
 *
 * @param strength
 *          The amount of particles to show.
 */
fun Location.showGrowthParticle(strength: Int = 5) {
    world?.playEffect(this, Effect.VILLAGER_PLANT_GROW, strength)
}

/**
 * Shows healing particles at the given entity.
 *
 * @param hearts
 *          The amount of hearts to show.
 * @param fuzzing
 *          Maximum deviation in the x and z direction.
 */
fun Location.showHealParticle(hearts: Int, fuzzing: Double = 0.3) {
    world?.spawnParticle(Particle.HEART,
            x.fuzz(fuzzing),
            y + 2.0.fuzz(0.5),
            z.fuzz(fuzzing),
            hearts
    )
}

/**
 * Shows a music note particle on this location.
 */
fun Location.showMusicNoteParticle() = clone().add(0.5, 0.5, 0.5).let {
    world?.spawnParticle(Particle.NOTE, this, 1)
}

/**
 * Shows a firework shooty particle.
 */
fun Location.showFireworkSpark(count: Int = 15) = clone().add(0.5, 0.5, 0.5).let {
    world?.spawnParticle(Particle.FIREWORKS_SPARK, x, y, z, count, 0.0, 0.0, 0.0)
}
