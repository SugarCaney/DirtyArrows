package nl.sugcube.dirtyarrows.region

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.util.toLocation
import nl.sugcube.dirtyarrows.util.toLocationString
import org.bukkit.Location
import java.util.logging.Level

/**
 * @author SugarCaney
 */
open class RegionManager(private val plugin: DirtyArrows) {

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
     * Get all registered regions.
     */
    val allRegions: List<Region>
        get() = registeredRegions.values.toList()

    /**
     * The names of all registered regions.
     */
    val allNames: Collection<String>
        get() = registeredRegions.keys.toList()

    /**
     * Get the amount of regions that have been registered.
     */
    val size: Int
        get() = registeredRegions.size

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
     * Creates a region and register it to the Region Manager.
     * Does not update the data configuration.
     *
     * @param registerName
     *          The name of the region to create, must only contain A-Z, a-z, 0-9, _, characters.
     * @return The created region, or `null` when the region could not be created.
     */
    fun createRegion(registerName: String): Region? {
        if (position1 == null || position2 == null) return null
        require(Region.NAME_REGEX.matches(registerName)) { "Invalid region name: '$registerName'" }

        val region = Region(position1!!, position2!!, registerName)
        registeredRegions[registerName] = region
        return region
    }

    /**
     * Unregisters the given region and also removes it from the data file.
     */
    fun removeRegion(name: String) {
        if (regionExists(name).not()) return
        registeredRegions.remove(name)
        plugin.data.set("regions.$name", null)
        plugin.configurationManager.saveData()
    }

    /**
     * Gets position #`positionNumber`.
     *
     * @param positionNumber
     *          Either 1 or 2 for position 1 and 2 respectively.
     * @return The set position, or `null` when the position hasn't been set.
     */
    fun getSelection(positionNumber: Int) = when (positionNumber) {
        1 -> position1
        2 -> position2
        else -> error("Invalid location number '$positionNumber', only 1|2 allowed.")
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
        val data = plugin.data

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

        plugin.logger.log(Level.INFO, "Loaded ${registeredRegions.size} regions.")
    }

    /**
     * Saves all registered regions to the data.yml
     */
    fun saveRegions() {
        val data = plugin.data
        registeredRegions.forEach { (regionName, region) ->
            val location1 = region.getLocation(1).toLocationString()
            val location2 = region.getLocation(2).toLocationString()

            data.set("regions.$regionName.pos1", location1)
            data.set("regions.$regionName.pos2", location2)
        }

        plugin.configurationManager.saveData()

        plugin.logger.log(Level.INFO, "Saved ${registeredRegions.size} regions to data.yml.")
    }

    /**
     * Checks whether the given location is within a region + margin.
     * Not counting height.
     *
     * @return The first region the location is in, or `null` if the location is not within a region + margin.
     */
    fun isWithinARegionXZMargin(location: Location, margin: Double): Region? {
        return registeredRegions.values.firstOrNull { it.isWithinXZMargin(location, margin) }
    }

    /**
     * Checks whether the given location is within a region + margin.
     *
     * @return The first region the location is in, or `null` if the location is not within a region + margin.
     */
    fun isWithinARegionMargin(location: Location, margin: Double): Region? {
        return registeredRegions.values.firstOrNull { it.isWithinMargin(location, margin) }
    }

    /**
     * Checks wether the given location is within one of the registered regions.
     *
     * @return The first region the location is in, or `null` if the location is not within a region.
     */
    fun isWithinARegion(location: Location) = isWithinARegionMargin(location, margin = 0.0)
}