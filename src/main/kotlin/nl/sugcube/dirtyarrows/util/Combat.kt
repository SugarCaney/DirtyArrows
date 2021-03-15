package nl.sugcube.dirtyarrows.util

import kotlin.math.ceil
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Rolls whether a hit should be critical.
 */
fun rollCritical(chance: Double = 0.25) = Random.nextDouble() < chance

/**
 * Calculates the amount of extra bonus damage (additive) that must be added for a critical hit.
 */
fun criticalDamage(baseDamage: Double): Double {
    val max = (baseDamage / 2.0).roundToInt() + 1
    return Random.nextInt(0, max).toDouble()
}

/**
 * Calculates how many damage an arrow should do, based on vanilla behaviour.
 *
 * @param arrowVelocity
 *          How quickly the arrow flies (velocity length).
 * @param criticalHitChance
 *          How much chance there is for the damage to be critical.
 * @param powerLevel
 *          The power level enchantment of the bow, or 0 for now power level.
 * @return The damage to deal.
 */
fun arrowDamage(arrowVelocity: Double, criticalHitChance: Double = 0.25, powerLevel: Int = 0): Double {
    val baseDamage = ceil(2.0 * arrowVelocity)
    val powerMultiplier = powerDamageMultiplier(powerLevel)
    val criticalDamage = if (rollCritical(criticalHitChance)) criticalDamage(baseDamage) else 0.0
    return (baseDamage + criticalDamage) * powerMultiplier
}

/**
 * With how much to multiply arrow damage given the power enchantment level.
 */
fun powerDamageMultiplier(powerLevel: Int = 0) = when (powerLevel) {
    0 -> 1.0
    else -> 1 + 0.5 * (powerLevel + 1)
}