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
    val name: String
) {

    init {
        require(name.matches(NAME_REGEX)) { "Invalid name: '$name'" }
    }

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

    /**
     * Location 1.
     */
    operator fun component1() = location1

    /**
     * Location 2.
     */
    operator fun component2() = location2

    companion object {

        /**
         * Regex that matches correctly against a valid name.
         */
        val NAME_REGEX = Regex("^[A-Za-z\\d_]+$")
    }
}

/**
 * Checks whether the given location is within the region, expanded with a certain margin.
 * Only checks the XZ plane, ignores height.
 *
 * @param location
 *          The location to check if it lies within the region (+ margin).
 * @param margin
 *          The distance (in blocks) outside of the region that should still evaluate `true`.
 * @return `true` if the location lies within the region + margin, `false` otherwise.
 */
fun Region.isWithinXZMargin(location: Location, margin: Double): Boolean {
    val (position1, position2) = this

    // Only return `true` when in the same world.
    if (position1.world != position2.world) return false
    if (location.world != position1.world) return false

    var p1x = position1.x
    var p1z = position1.z
    var p2x = position2.x
    var p2z = position2.z

    if (p1x <= p2x) {
        p1x -= margin
        p2x += margin
    }
    else {
        p1x += margin
        p2x -= margin
    }

    if (p1z <= p2z) {
        p1z -= margin
        p2z += margin
    }
    else {
        p1z += margin
        p2z -= margin
    }

    if (p2x <= location.x && location.x < p1x || p1x <= location.x && location.x < p2x) {
        if (p2z <= location.z && location.z < p1z || p1z <= location.z && location.z < p2z) {
            return true
        }
    }

    return false
}

/**
 * Checks whether the given location is within the region, expanded with a certain margin.
 *
 * @param location
 *          The location to check if it lies within the region (+ margin).
 * @param margin
 *          The distance (in blocks) outside of the region that should still evaluate `true`.
 * @return `true` if the location lies within the region + margin, `false` otherwise.
 */
fun Region.isWithinMargin(location: Location, margin: Double): Boolean {
    val (position1, position2) = this

    // Only return `true` when in the same world.
    if (position1.world != position2.world) return false
    if (location.world != position1.world) return false

    var p1x = position1.x
    var p1y = position1.y
    var p1z = position1.z
    var p2x = position2.x
    var p2y = position2.y
    var p2z = position2.z

    if (p1x <= p2x) {
        p1x -= margin
        p2x += margin
    }
    else {
        p1x += margin
        p2x -= margin
    }

    if (p1y <= p2y) {
        p1y -= margin
        p2y += margin
    }
    else {
        p1y += margin
        p2y -= margin
    }

    if (p1z <= p2z) {
        p1z -= margin
        p2z += margin
    }
    else {
        p1z += margin
        p2z -= margin
    }

    if (p2x <= location.x && location.x < p1x || p1x <= location.x && location.x < p2x) {
        if (p2y <= location.y && location.y < p1y || p1y <= location.y && location.y < p2y) {
            if (p2z <= location.z && location.z < p1z || p1z <= location.z && location.z < p2z) {
                return true
            }
        }
    }

    return false
}

/**
 * Checks whether the given location is within this region.
 *
 * @param location
 *          The location to check if it lies within the region.
 * @return `true` if the location lies within the region, `false` otherwise.
 */
fun Region.isWithinRegion(location: Location) = isWithinMargin(location, margin = 0.0)