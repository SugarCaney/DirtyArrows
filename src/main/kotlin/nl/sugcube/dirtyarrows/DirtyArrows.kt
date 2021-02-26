package nl.sugcube.dirtyarrows

import nl.sugcube.dirtyarrows.ability.*
import nl.sugcube.dirtyarrows.command.DirtyArrowsCommandManager
import nl.sugcube.dirtyarrows.listener.*
import nl.sugcube.dirtyarrows.recipe.registerArrowRecipes
import nl.sugcube.dirtyarrows.region.RegionManager
import nl.sugcube.dirtyarrows.util.Help
import nl.sugcube.dirtyarrows.util.Update
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.entity.Entity
import org.bukkit.entity.FallingBlock
import org.bukkit.entity.Player
import org.bukkit.entity.Projectile
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Level

/**
 * @author SugarCaney
 */
class DirtyArrows : JavaPlugin() {

    /**
     * The plugin's version number as defined in plugin.yml
     */
    val version: String
        get() = description.version

    /**
     * The config file reference.
     */
    var configurationFile = File(dataFolder, "config.yml")

    /**
     * The data file reference.
     */
    private var dataFile = File(dataFolder, "data.yml")

    /**
     * The actual loaded data configuration.
     *
     * `null` when the configuration has not been loaded.
     */
    private var data: FileConfiguration? = null

    var al = ArrowListener(this)
    var el = EnchantmentListener(this)
    var pjl = PlayerJoinListener(this)
    var pdl = PlayerDamageListener(this)
    var enl = EntityListener(this)
    var help = Help(this)
    @JvmField var rm = RegionManager(this)
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

    /**
     * Reloads the plugin configuration.
     */
    fun reloadConfiguration() {
        reloadConfig()
        setArrowRecipes()
    }

    /**
     * Adds the custom arrow recipes to the server (configurable arrow amount per craft).
     */
    fun setArrowRecipes() {
        server.resetRecipes()
        server.registerArrowRecipes(config.getInt("arrow-recipe-amount"))
    }

    /**
     * Get the data configuration. Loads the data when the data configuration is not present.
     */
    fun getData(): FileConfiguration? {
        if (data == null) {
            reloadData()
        }
        return data
    }

    /**
     * Reloads the data.yml file.
     */
    fun reloadData() {
        data = YamlConfiguration.loadConfiguration(dataFile)
        getResource("data.yml")?.let { defaultConfigStream ->
            val defaultConfig = YamlConfiguration.loadConfiguration(defaultConfigStream.bufferedReader())
            data!!.defaults = defaultConfig
        }
    }

    /**
     * Saves the data configuration to the data file.
     */
    fun saveData() {
        if (data == null) return
        try {
            getData()!!.save(dataFile)
        }
        catch (ex: Exception) {
            logger.log(Level.SEVERE, "Could not save config to $dataFile", ex)
        }
    }

    override fun onEnable() {
        /*
		 * Load config.yml and data.yml
		 */
        val file = File(dataFolder.toString() + File.separator + "config.yml")
        if (!file.exists()) {
            try {
                config.options().copyDefaults(true)
                saveConfig()
                logger.info("Generated config.yml succesfully!")
            } catch (e: Exception) {
                logger.info("Failed to generate config.yml!")
            }
        }
        val df = File(dataFolder.toString() + File.separator + "data.yml")
        if (!df.exists()) {
            try {
                reloadData()
                saveData()
                logger.info("Generated data.yml succesfully!")
            } catch (e: Exception) {
                logger.info("Failed to generate data.yml!")
            }
        }

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

        val arrow =
            ShapedRecipe(ItemStack(Material.ARROW, config.getInt("arrow-recipe-amount"))).shape(" * ", " # ", " % ")
                .setIngredient('*', Material.FLINT).setIngredient('#', Material.STICK)
                .setIngredient('%', Material.FEATHER)
        val arrow2 =
            ShapedRecipe(ItemStack(Material.ARROW, config.getInt("arrow-recipe-amount"))).shape("*  ", "#  ", "%  ")
                .setIngredient('*', Material.FLINT).setIngredient('#', Material.STICK)
                .setIngredient('%', Material.FEATHER)
        val arrow3 =
            ShapedRecipe(ItemStack(Material.ARROW, config.getInt("arrow-recipe-amount"))).shape("  *", "  #", "  %")
                .setIngredient('*', Material.FLINT).setIngredient('#', Material.STICK)
                .setIngredient('%', Material.FEATHER)
        server.addRecipe(arrow)
        server.addRecipe(arrow2)
        server.addRecipe(arrow3)
        server.scheduler.scheduleSyncRepeatingTask(this, Timer(this), 0, 1)
        server.scheduler.scheduleSyncRepeatingTask(this, Airstrike(this), 5, 5)
        server.scheduler.scheduleSyncRepeatingTask(this, Particles(this), 2, 2)
        server.scheduler.scheduleSyncRepeatingTask(this, Airship(this), 2, 2)
        server.scheduler.scheduleSyncRepeatingTask(this, iron, 5, 5)
        server.scheduler.scheduleSyncRepeatingTask(this, curse, 20, 20)
        server.scheduler.scheduleSyncRepeatingTask(this, frozenListener, 20, 20)
        rm.loadRegions()
        logger.info("[DirtyArrows] DirtyArrows has been enabled!")
        logger.info("[DirtyArrows] 42 Bastards have been loaded")
        logger.info("[DirtyArrows] 3 recipes have been loaded")

        /*
		 * Check for updatese
		 */if (this.config.getBoolean("updates.check-for-updates")) {
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
    }

    override fun onDisable() {
        rm.saveRegions()
        logger.info("[DirtyArrows] DirtyArrows has been disabled!")
    }
}