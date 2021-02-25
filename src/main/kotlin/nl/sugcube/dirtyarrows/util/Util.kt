@file:JvmName("DaUtil")

package nl.sugcube.dirtyarrows.util

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location

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
fun Location.toLocationString() = "${world.name}%$x%$y%$z%$pitch%$yaw"

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