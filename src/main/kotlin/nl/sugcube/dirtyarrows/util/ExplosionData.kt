package nl.sugcube.dirtyarrows.util

/**
 * @author SugarCaneu
 */
data class ExplosionData(

    /**
     * Explosion power (4.0 for TNT).
     */
    val power: Float,

    /**
     * Whether explosions can break blocks or not.
     */
    val breakBlocks: Boolean = true,

    /**
     * Whether explosions can set blocks on fire or not.
     */
    val setOnFire: Boolean = false
)