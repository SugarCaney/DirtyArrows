package nl.sugcube.dirtyarrows

import nl.sugcube.dirtyarrows.ability.CurseListener
import nl.sugcube.dirtyarrows.ability.FrozenListener
import nl.sugcube.dirtyarrows.ability.Iron
import nl.sugcube.dirtyarrows.bow.BowManager
import nl.sugcube.dirtyarrows.command.DirtyArrowsCommandManager
import nl.sugcube.dirtyarrows.listener.*
import nl.sugcube.dirtyarrows.recipe.RecipeManager
import nl.sugcube.dirtyarrows.region.RegionManager
import nl.sugcube.dirtyarrows.util.Error
import nl.sugcube.dirtyarrows.util.Help
import nl.sugcube.dirtyarrows.util.Update
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Entity
import org.bukkit.entity.FallingBlock
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import java.util.*
import java.util.concurrent.ConcurrentHashMap
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

    var al = ArrowListener(this)
    var el = EnchantmentListener(this)
    var pjl = PlayerJoinListener(this)
    var pdl = PlayerDamageListener(this)
    var enl = EntityListener(this)
    var help = Help(this)
    var iron = Iron(this)
    var curse = CurseListener(this)
    var frozenListener = FrozenListener(this)
    var anvilListener = AnvilListener(this)

    @JvmField var slow: MutableList<Projectile> = ArrayList()
    @JvmField var airstrike: MutableList<Projectile> = ArrayList()
    @JvmField var airship: MutableList<Projectile> = ArrayList()
    @JvmField var noFallDamage: MutableList<UUID> = ArrayList()
    @JvmField var slowVec: MutableList<Vector> = ArrayList()
    @JvmField var cursed = ConcurrentHashMap<Entity, Int>()
    @JvmField var frozen = ConcurrentHashMap<Entity, Int>()
    @JvmField var noInteract: MutableList<Player> = ArrayList()
    @JvmField var particleExploding: MutableList<Projectile> = ArrayList()
    @JvmField var particleFire: MutableList<Projectile> = ArrayList()
    @JvmField var particleLava: MutableList<FallingBlock> = ArrayList()
    @JvmField var particleWater: MutableList<FallingBlock> = ArrayList()
    @JvmField var ice: MutableList<Int> = ArrayList()
    @JvmField var iceParticle: MutableList<Projectile> = ArrayList()
    @JvmField var anvils = ConcurrentHashMap<FallingBlock, Int>()

    /**
     * Manages if DA is turned on or off for what entities.
     * DA should only apply the effects of the bows when DA is enabled.
     */
    val activationManager = ActivationManager(this::isMinigameVersion)

    /**
     * Helper for showing error messages.
     */
    val error = Error(this)

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
        val commandManager = DirtyArrowsCommandManager(this)
        getCommand("dirtyarrows").apply {
            executor = commandManager
            tabCompleter = commandManager
        }
    }

    /**
     * Registers all events.
     */
    private fun registerEvents() = with(server.pluginManager) {
//        val plugin = this@DirtyArrows
//        registerEvents(al, plugin)
//        registerEvents(el, plugin)
//        registerEvents(enl, plugin)
//        registerEvents(pjl, plugin)
//        registerEvents(pdl, plugin)
//        registerEvents(iron, plugin)
//        registerEvents(frozenListener, plugin)
//        registerEvents(anvilListener, plugin)
    }

    /**
     * Checks if there is an update available, and prints the result of this check to the console.
     * Does nothing when the 'updates.check-for-updates' setting is set to `false`.
     */
    private fun checkForUpdates() {
        if (config.getBoolean("updates.check-for-updates").not()) return

        if (Update(BUKKIT_DEV_PROJECT_ID, description.version).query()) {
            logger.log(Level.INFO, "A new version of DirtyArrows is available!")
        }
        else logger.log(Level.INFO, "DirtyArrows is up-to-date!")
    }

    override fun onEnable() {
        configurationManager.initialise()
        registerCommands()
        registerEvents()
        recipeManager.reloadRecipes()
        regionManager.loadRegions()
        bowManager.reload()
//        server.scheduler.scheduleSyncRepeatingTask(this, Slow(this), 0, 1)
//        server.scheduler.scheduleSyncRepeatingTask(this, Airstrike(this), 5, 5)
//        server.scheduler.scheduleSyncRepeatingTask(this, Particles(this), 2, 2)
//        server.scheduler.scheduleSyncRepeatingTask(this, Airship(this), 2, 2)
//        server.scheduler.scheduleSyncRepeatingTask(this, iron, 5, 5)
//        server.scheduler.scheduleSyncRepeatingTask(this, curse, 20, 20)
//        server.scheduler.scheduleSyncRepeatingTask(this, frozenListener, 20, 20)
        checkForUpdates()

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