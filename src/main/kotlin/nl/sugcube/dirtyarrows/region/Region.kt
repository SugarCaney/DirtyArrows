package nl.sugcube.dirtyarrows.region

import org.bukkit.Location

/**
 * Represents a rectangluar DirtyArrows protection region.
 *
 * @author SugarCaney
 */
class Region @JvmOverloads constructor(

    /**
     * The first corner of the region.
     */
    private var location1: Location,

    /**
     * The second corner of the region.
     */
    private var location2: Location,

    /**
     * The name for the region. Contains only A-Z, a-z, 0-9 and _.
     */
    var name: String? = null
) {

    /**
     * Sets a corner location of the region.
     *
     * @param location
     *          The location value.
     * @param locationNumber
     *          `1` for location 1, and `2` for location 2.
     */
    fun setLocation(location: Location, locationNumber: Int) = when (locationNumber) {
        1 -> location1 = location
        2 -> location2 = location
        else -> error("Invalid location number '$locationNumber', only 1|2 allowed.")
    }

    /**
     * Get a corner location of the region.
     *
     * @param locationNumber
     *          `1` for location 1, and `2` for location 2.
     */
    fun getLocation(locationNumber: Int) = when (locationNumber) {
        1 -> location1
        2 -> location2
        else -> error("Invalid location number '$locationNumber', only 1|2 allowed.")
    }
}