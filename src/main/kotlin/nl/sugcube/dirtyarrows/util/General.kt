@file:JvmName("DaUtil")

package nl.sugcube.dirtyarrows.util

import nl.sugcube.dirtyarrows.Broadcast
import nl.sugcube.dirtyarrows.bow.ability.AwtColor
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.command.CommandSender
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.random.Random

/**
 * Converts a string to a location.
 *
 * Format (either):
 * * `(world)%(x)%(y)%(z)`, or
 * * `(world)%(x)%(y)%(z)%(pitch)%(yaw)`
 *
 * @return The parsed world location.
 */
fun String.toLocation(): Location? {
    val parts = split('%')
    val world = Bukkit.getWorld(parts[0]) ?: return null

    return Location(
        world,
        parts[1].toDoubleOrNull() ?: error("Invalid x coordinate: '${parts[1]}'"),
        parts[2].toDoubleOrNull() ?: error("Invalid y coordinate: '${parts[2]}'"),
        parts[3].toDoubleOrNull() ?: error("Invalid z coordinate: '${parts[3]}'")
    ).apply {
        // When a pitch/yaw is present, update the location.
        if (parts.size > 4) {
            pitch = parts[4].toFloatOrNull() ?: error("Invalid pitch: '${parts[4]}'")
            yaw = parts.getOrNull(5)?.toFloatOrNull() ?: error("Invalid yaw: '${parts[5]}'")
        }
    }
}

/**
 * Converts a location to a string.
 *
 * Format:
 * * `(world)%(x)%(y)%(z)%(pitch)%(yaw)`
 *
 * @return The string that represents the location.
 */
fun Location.toLocationString() = "${world?.name}%$x%$y%$z%$pitch%$yaw"

/**
 * Converts the location to a (x: X, y: Y, z: Z) string.
 */
fun Location.toCoordinateString() = "(x: ${floor(x)}, y: ${ceil(y)}, z: ${floor(z)})"

/**
 * Replaces all minecraft colour code macros (&1, &2, etc.) with ChatColors.
 */
fun String.applyColours() = replace("&0", ChatColor.BLACK.toString())
    .replace("&1", ChatColor.DARK_BLUE.toString())
    .replace("&2", ChatColor.DARK_GREEN.toString())
    .replace("&3", ChatColor.DARK_AQUA.toString())
    .replace("&4", ChatColor.DARK_RED.toString())
    .replace("&5", ChatColor.DARK_PURPLE.toString())
    .replace("&6", ChatColor.GOLD.toString())
    .replace("&7", ChatColor.GRAY.toString())
    .replace("&8", ChatColor.DARK_GRAY.toString())
    .replace("&9", ChatColor.BLUE.toString())
    .replace("&a", ChatColor.GREEN.toString())
    .replace("&b", ChatColor.AQUA.toString())
    .replace("&c", ChatColor.RED.toString())
    .replace("&d", ChatColor.LIGHT_PURPLE.toString())
    .replace("&e", ChatColor.YELLOW.toString())
    .replace("&f", ChatColor.WHITE.toString())
    .replace("&k", ChatColor.MAGIC.toString())
    .replace("&l", ChatColor.BOLD.toString())
    .replace("&m", ChatColor.STRIKETHROUGH.toString())
    .replace("&n", ChatColor.UNDERLINE.toString())
    .replace("&o", ChatColor.ITALIC.toString())
    .replace("&r", ChatColor.RESET.toString())

/**
 * Parses a hex string into a colour.
 * Throws an exception when it could not be parsed with a custom info message `sourceInfo`.
 */
fun String?.toColour(sourceInfo: String = ""): Color {
    if (this == null) {
        error("No string found to parse <null>" + if (sourceInfo.isBlank()) "" else " ($sourceInfo)")
    }

    try {
        val awt = AwtColor.decode(this)
        return Color.fromRGB(awt.red, awt.green, awt.blue)
    }
    catch (e: NumberFormatException) {
        error("Could not parse colour <$this>: ${e.message}" + if (sourceInfo.isBlank()) "" else " ($sourceInfo)")
    }
}

/**
 * Sends a message to the given command sender where all colour codes (&1, &2, ...) are applied.
 */
fun CommandSender.sendFormattedMessage(message: String) = sendMessage(message.applyColours())

/**
 * Sends a message with the error tag. Supports colour codes (&1, &2, ...).
 */
fun CommandSender.sendError(message: String) = sendFormattedMessage(Broadcast.TAG_ERROR + " $message")

/**
 * Get the online player with the given name.
 *
 * @param name
 *      The name of the online player, or `@r` for a random player.
 * @return `null` when there is no player with the given name.
 */
fun onlinePlayer(name: String) = if (name == "@r") {
    Bukkit.getOnlinePlayers().random()
} else Bukkit.getOnlinePlayers().firstOrNull { it.name == name }

/**
 * Performs the given [action] on each element that is not `null`.
 */
public inline fun <T> Iterable<T?>.forEachNotNull(action: (T) -> Unit): Unit {
    for (element in this) {
        if (element != null) {
            action(element)
        }
    }
}

/**
 * Loops over x, y, z range.
 */
inline fun forXYZ(xRange: IntProgression, yRange: IntProgression, zRange: IntProgression, action: (dx: Int, dy: Int, dz: Int) -> Unit) {
    for (dx in xRange) {
        for (dy in yRange) {
            for (dz in zRange) {
                action(dx, dy, dz)
            }
        }
    }
}

/**
 * Loops over x, y, z range.
 */
inline fun forXYZ(range: IntProgression, action: (dx: Int, dy: Int, dz: Int) -> Unit) = forXYZ(range, range, range, action)

/**
 * Fuzzes this number: creates a random number in `[this - maxFuzz, this + maxFuzz]`.
 */
fun Double.fuzz(maxFuzz: Double) = this - maxFuzz + 2 * Random.nextDouble() * maxFuzz

/**
 * Fuzzes this number: creates a random number in `[this - maxFuzz, this + maxFuzz]`.
 */
fun Float.fuzz(maxFuzz: Float) = this - maxFuzz + 2 * Random.nextFloat() * maxFuzz

/**
 * Fuzzes this number: creates a random number `this-maxFuzz <= this <= this+maxFuzz`.
 */
fun Long.fuzz(maxFuzz: Long) = Random.nextLong(this - maxFuzz, this + maxFuzz + 1)

/**
 * Fuzzes this number: creates a random number `this-maxFuzz <= this <= this+maxFuzz`.
 */
fun Int.fuzz(maxFuzz: Int) = Random.nextInt(this - maxFuzz, this + maxFuzz + 1)

/**
 * Casts this type as `otherType` and executes the given block on it. Then returns the source type.
 * This is useful for using ItemMeta/BlockMeta and casting it temporarily to an implemented type.
 */
inline fun <T, reified U> T.useAs(crossinline block: (U) -> Unit): T {
    val other = this as? U ?: return this
    block(other)
    return this
}