package nl.sugcube.dirtyarrows

import nl.sugcube.dirtyarrows.ability.*
import nl.sugcube.dirtyarrows.command.DirtyArrowsCommandManager
import nl.sugcube.dirtyarrows.listener.*
import nl.sugcube.dirtyarrows.recipe.RecipeManager
import nl.sugcube.dirtyarrows.region.RegionManager
import nl.sugcube.dirtyarrows.util.Help
import nl.sugcube.dirtyarrows.util.Update
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Entity
import org.bukkit.entity.FallingBlock
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import java.util.*
import java.util.concurrent.ConcurrentHashMap

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
     * Manages the activation of DA by entities.
     * DA should only apply the effects of the bows when DA is enabled.
     */
    val activationManager = ActivationManager(this::isMinigameVersion)

    /**
     * Whether the plugin runs in a DirtyArrows minigame.
     *
     * @return `true` when part of a minigame, `false` otherwise.
     */
    fun isMinigameVersion() = false

    override fun onEnable() {
        configurationManager.initialise()

        val pm = server.pluginManager
        pm.registerEvents(al, this)
        pm.registerEvents(el, this)
        pm.registerEvents(enl, this)
        pm.registerEvents(pjl, this)
        pm.registerEvents(pdl, this)
        pm.registerEvents(iron, this)
        pm.registerEvents(curse, this)
        pm.registerEvents(frozenListener, this)
        pm.registerEvents(anvilListener, this)

        val commandManager = DirtyArrowsCommandManager(this)
        getCommand("da").apply {
            executor = commandManager
            tabCompleter = commandManager
        }
        getCommand("dirtyarrows").apply {
            executor = commandManager
            tabCompleter = commandManager
        }

        recipeManager.reloadRecipes()

        server.scheduler.scheduleSyncRepeatingTask(this, Timer(this), 0, 1)
        server.scheduler.scheduleSyncRepeatingTask(this, Airstrike(this), 5, 5)
        server.scheduler.scheduleSyncRepeatingTask(this, Particles(this), 2, 2)
        server.scheduler.scheduleSyncRepeatingTask(this, Airship(this), 2, 2)
        server.scheduler.scheduleSyncRepeatingTask(this, iron, 5, 5)
        server.scheduler.scheduleSyncRepeatingTask(this, curse, 20, 20)
        server.scheduler.scheduleSyncRepeatingTask(this, frozenListener, 20, 20)
        regionManager.loadRegions()

        /*
		 * Check for updatese
		 */
        if (this.config.getBoolean("updates.check-for-updates")) {
            val uc = Update(57131, description.version)
            if (uc.query()) {
                Bukkit.getConsoleSender().sendMessage(
                    ChatColor.GREEN.toString() + "[DirtyArrows] A new version of DirtyArrows is " +
                            "avaiable! Get it at the BukkitDev page!"
                )
            } else {
                Bukkit.getConsoleSender().sendMessage("[DirtyArrows] DirtyArrows is up-to-date!")
            }
        }

        logger.info("[DirtyArrows] DirtyArrows has been enabled!")
    }

    override fun onDisable() {
        regionManager.saveRegions()
        logger.info("[DirtyArrows] DirtyArrows has been disabled!")
    }
}