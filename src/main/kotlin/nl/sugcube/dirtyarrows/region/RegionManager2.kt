package nl.sugcube.dirtyarrows.region

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.util.toLocation
import org.bukkit.Location

/**
 * @author SugarCaney
 */
open class RegionManager2(private val plugin: DirtyArrows) {

    /**
     * Contains all known regions. Maps the region name/id to the actual region object.
     */
    private val registeredRegions = HashMap<String, Region>()

    /**
     * The first selected position.
     */
    private var position1: Location? = null

    /**
     * The second selected position.
     */
    private var position2: Location? = null

    /**
     * Checks whether a region is registered with the given name.
     */
    fun regionExists(name: String): Boolean = regionByName(name) != null

    /**
     * Get the registered region with the given name.
     *
     * @param name
     *          The name of the region to lookup.
     * @return The region or `null` if there is no region with the given name.
     */
    fun regionByName(name: String) = registeredRegions[name]

    /**
     * Unregisters the given region and also removes it from the data file.
     */
    fun removeRegion(name: String) {
        if (regionExists(name).not()) return
        registeredRegions.remove(name)
        plugin.getData()?.set("regions.$name", null) ?: error("No data configuration has been loaded.")
        plugin.saveData()
    }

    /**
     * Sets position #`positionNumber` to the given location.
     *
     * @param positionNumber
     *          Either 1 or 2 for position 1 and 2 respectively.
     * @param location
     *          The location to set the position to.
     */
    fun setSelection(positionNumber: Int, location: Location) = when (positionNumber) {
        1 -> position1 = location
        2 -> position2 = location
        else -> error("Invalid location number '$positionNumber', only 1|2 allowed.")
    }

    /**
     * Loads all the regions from the data configuration and registers them.
     * Overwrites all current regions.
     */
    fun loadRegions() {
        val data = plugin.getData() ?: error("No data configuration available.")

        if (data.isSet("regions").not()) {
            registeredRegions.clear()
            return
        }

        data.getConfigurationSection("regions").getKeys(false).forEach { key ->
            val path = "regions.$key"
            val position1 = data.getString("$path.pos1").toLocation() ?: return@forEach
            val position2 = data.getString("$path.pos2").toLocation() ?: return@forEach
            val region = Region(position1, position2, key)
            registeredRegions[key] = region
        }
    }
}