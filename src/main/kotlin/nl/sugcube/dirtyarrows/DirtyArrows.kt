package nl.sugcube.dirtyarrows

import nl.sugcube.dirtyarrows.bow.AutoActivation
import nl.sugcube.dirtyarrows.bow.BowManager
import nl.sugcube.dirtyarrows.command.DirtyArrowsCommandManager
import nl.sugcube.dirtyarrows.effect.*
import nl.sugcube.dirtyarrows.recipe.RecipeManager
import nl.sugcube.dirtyarrows.region.RegionManager
import nl.sugcube.dirtyarrows.util.Update
import org.bukkit.Bukkit
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level

/**
 * DirtyArrows (DA) bukkit plugin.
 *
 * @author SugarCaney
 */
class DirtyArrows : JavaPlugin() {

    /**
     * The plugin's version number as defined in plugin.yml
     */
    val version: String
        get() = description.version

    /**
     * Handles all configuration and data files.
     */
    val configurationManager = ConfigurationManager(this)

    /**
     * The data file configuration.
     */
    val data: FileConfiguration
        get() = configurationManager.data

    /**
     * Manages all DA commands.
     */
    val commandManager = DirtyArrowsCommandManager(this)

    /**
     * Manages all DA protection regions.
     */
    val regionManager = RegionManager(this)

    /**
     * Manages all custom recipes of DA.
     */
    val recipeManager = RecipeManager(this)

    /**
     * Manages all registered bows.
     */
    val bowManager = BowManager(this)

    /**
     * Manages if DA is turned on or off for what entities.
     * DA should only apply the effects of the bows when DA is enabled.
     */
    val activationManager = ActivationManager(this::isMinigameVersion)

    /**
     * Shows the help menu messages.
     */
    val help = Help(this)

    /**
     * Whether the plugin runs in a DirtyArrows minigame.
     *
     * @return `true` when part of a minigame, `false` otherwise.
     */
    fun isMinigameVersion() = config.getBoolean("minigame-mode")

    /**
     * Registers all DA commands.
     */
    private fun registerCommands() {
        getCommand("dirtyarrows").apply {
            executor = commandManager
            tabCompleter = commandManager
        }
    }

    /**
     * Registers all events.
     */
    private fun registerEvents() = with(server.pluginManager) {
        val plugin = this@DirtyArrows
        registerEvents(UpdateCheck(plugin), plugin)
        registerEvents(AutoActivation(plugin), plugin)
        registerEvents(AnvilLevelModification(plugin), plugin)
        registerEvents(Headshot(plugin), plugin)
        registerEvents(LootingOnBow(plugin), plugin)
        registerEvents(ExplosionProtection(plugin), plugin)
        registerEvents(Blood(plugin), plugin)
        registerEvents(DamageEffects(), plugin)
        registerEvents(ZombieFlint(plugin), plugin)
        registerEvents(recipeManager, plugin)
    }

    /**
     * Checks if there is an update available, and prints the result of this check to the console.
     * Does nothing when the 'updates.check-for-updates' setting is set to `false`.
     */
    private fun checkForUpdates() {
        if (config.getBoolean("updates.check-for-updates").not()) return

        val updateChecker = Update(BUKKIT_DEV_PROJECT_ID, description.version)
        if (updateChecker.query()) {
            logger.log(Level.INFO, "A new version of DirtyArrows is available: v${updateChecker.latestVersion}")
        }
        else logger.log(Level.INFO, "DirtyArrows is up-to-date!")
    }

    /**
     * Enables Dirty Arrows for all online players that have the dirtyarrows permission.
     */
    private fun enableForAllPlayers() = Bukkit.getOnlinePlayers().forEach { player ->
        if (config.getBoolean("auto-enable").not()) return
        if (player.hasPermission("dirtyarrows").not()) return@forEach

        activationManager.activateFor(player)
        if (config.getBoolean("show-enable-message")) {
            player.sendMessage(Broadcast.enabledMessage(this, enabled = true))
        }
    }

    /**
     * Create all necessary directories for the plugin to work.
     */
    private fun createDirectories() {
        if (dataFolder.exists().not()) {
            dataFolder.mkdirs()
        }
    }

    override fun onEnable() {
        createDirectories()
        configurationManager.initialise()
        registerCommands()
        registerEvents()
        regionManager.loadRegions()
        bowManager.reload()
        checkForUpdates()
        enableForAllPlayers()

        logger.info("DirtyArrows has been enabled!")
    }

    override fun onDisable() {
        regionManager.saveRegions()

        logger.info("DirtyArrows has been disabled!")
    }

    companion object {

        /**
         * The project ID of the plugin on BukkitDev.
         */
        const val BUKKIT_DEV_PROJECT_ID = 57131
    }
}