package nl.sugcube.dirtyarrows

import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import java.io.File
import java.io.IOException
import java.util.logging.Level

/**
 * @author SugarCaney
 */
open class ConfigurationManager(private val plugin: DirtyArrows) {

    /**
     * Reference to the file storing the configuration file.
     */
    private val configFile = File(plugin.dataFolder, "config.yml")

    /**
     * Reference to the file storing the data file.
     */
    private val dataFile = File(plugin.dataFolder, "data.yml")

    /**
     * The configuration file.
     */
    val config: FileConfiguration = plugin.config

    /**
     * The data file.
     */
    var data: FileConfiguration = loadData()
        private set

    /**
     * Sets up all configurations managed by this Configuration Manager.
     */
    fun initialise() {
        initialiseConfiguration()
        initialiseData()
    }

    /**
     * Sets up the configuration file for use. Copies defaults when needed.
     */
    fun initialiseConfiguration() {
        if (configFile.exists().not()) {
            copyConfigDefaults()
        }
        loadConfig()
    }

    /**
     * Sets up the data file for use.
     */
    fun initialiseData() {
        if (dataFile.exists().not()) {
            dataFile.createNewFile()
        }
    }

    /**
     * Sets all configu values to the config values provided by the default configuration file.
     */
    fun copyConfigDefaults() {
        if (configFile.exists().not()) {
            val configData = javaClass.getResourceAsStream("/config.yml").bufferedReader().readText()
            configFile.writeText(configData)
            plugin.logger.log(Level.INFO, "Extracted default configuration file (config.yml)")
        }
    }

    /**
     * Loads the configuration file.
     */
    fun loadConfig() {
        plugin.reloadConfig()
    }

    /**
     * Saves the current config state to the configuration file.
     */
    fun saveConfig() {
        plugin.saveConfig()
    }

    /**
     * Loads the data file.
     *
     * @return The loaded data file configuration.
     */
    fun loadData(): FileConfiguration {
        data = YamlConfiguration.loadConfiguration(dataFile)
        return data
    }

    /**
     * Saves the current data state to the data file.
     */
    fun saveData() {
        try {
            data.save(dataFile)
        } catch (ioe: IOException) {
            plugin.logger.log(Level.SEVERE, "Could not save data file to $dataFile", ioe)
        }
    }
}