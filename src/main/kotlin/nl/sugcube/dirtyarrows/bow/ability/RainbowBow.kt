package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.fuzz
import nl.sugcube.dirtyarrows.util.getFloat
import nl.sugcube.dirtyarrows.util.showColoredDust
import org.bukkit.Color

// Prevent clash with org.bukkit.Color.
typealias AwtColor = java.awt.Color

/**
 * Shoots a very, very slow arrow that One hit KO's everything.
 *
 * @author SugarCaney
 */
open class RainbowBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.RAINBOW,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "So pretty."
) {

    /**
     * Saturation of the colours.
     */
    val saturation = config.getFloat("$node.saturation")

    /**
     * Brightness of the colours.
     */
    val brightness = config.getFloat("$node.brightness")

    /**
     * How many particles to spawn per tick.
     */
    val particleCount = config.getInt("$node.particle-count")

    /**
     * How quickly to scroll through all the colours: multiplier for the tick counter.
     */
    val scrollSpeed = config.getInt("$node.scroll-speed")

    override fun particle(tickNumber: Int) {
        val colour = colourAtTick(tickNumber)

        arrows.forEach { arrow ->
            repeat(particleCount) {
                arrow.location.fuzz(0.15).showColoredDust(colour, 1)
            }
        }
    }

    /**
     * Get the colour for this tick.
     */
    private fun colourAtTick(tickNumber: Int): Color {
        val rgb = AwtColor.HSBtoRGB(hue(tickNumber), saturation, brightness)
        val color = AwtColor(rgb)
        return Color.fromRGB(color.red, color.green, color.blue)
    }

    /**
     * Get the hue for this tick.
     */
    private fun hue(tickNumber: Int) = ((tickNumber * scrollSpeed) % 360) / 360f
}