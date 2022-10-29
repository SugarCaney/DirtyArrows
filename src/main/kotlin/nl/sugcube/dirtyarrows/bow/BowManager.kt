package nl.sugcube.dirtyarrows.bow

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.ability.*
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import java.util.logging.Level

/**
 * Tracks all bow effects. Iterates over all registered bow types.
 *
 * @author SugarCaney
 */
open class BowManager(private val plugin: DirtyArrows): Iterable<BowType>, Listener {

    /**
     * Contains all available bows.
     */
    private val bows = HashMap<BowType, BowAbility>()

    /**
     * Keeps track of all scheduled task IDs. Mapped from each bow type.
     */
    private val tasks = HashMap<BowType, Int>()

    /**
     * Get all bow types that are registered.
     */
    val registeredTypes: Set<BowType>
        get() = bows.keys

    /**
     * Loads all enabled bows. Re-evaluates on second call.
     */
    fun reload() = with(plugin) {
        // Remove all registered bows, to overwrite with new ones.
        unload()
        loadAbilities()

        plugin.server.pluginManager.registerEvents(this@BowManager, plugin)

        bows.forEach { (bowType, ability) ->
            plugin.server.pluginManager.registerEvents(ability, plugin)

            // Cache task ID to be canceled on reload.
            val taskId = plugin.server.scheduler.scheduleSyncRepeatingTask(plugin, ability, 0L, 1L)
            tasks[bowType] = taskId
        }

        logger.log(Level.INFO, "Loaded ${bows.size} bows.")
    }

    /**
     * Adds all enabled bow ability implementations to [bows].
     */
    private fun loadAbilities() {
        ExplodingBow(plugin).load()
        LightningBow(plugin).load()
        CluckyBow(plugin).load()
        EnderBow(plugin).load()
        TreeBow(plugin, TreeBow.Tree.OAK).load()
        TreeBow(plugin, TreeBow.Tree.SPRUCE).load()
        TreeBow(plugin, TreeBow.Tree.BIRCH).load()
        TreeBow(plugin, TreeBow.Tree.JUNGLE).load()
        TreeBow(plugin, TreeBow.Tree.ACACIA).load()
        TreeBow(plugin, TreeBow.Tree.DARK_OAK).load()
        TreeBow(plugin, TreeBow.Tree.CRIMSON).load()
        TreeBow(plugin, TreeBow.Tree.WARPED).load()
        TreeBow(plugin, TreeBow.Tree.AZALEA).load()
        TreeBow(plugin, TreeBow.Tree.MANGROVE).load()
        BattyBow(plugin).load()
        NuclearBow(plugin).load()
        EnlightenedBow(plugin).load()
        RangedBow(plugin).load()
        MachineBow(plugin).load()
        VenomousBow(plugin).load()
        DisorientingBow(plugin).load()
        SwapBow(plugin).load()
        DrainBow(plugin).load()
        FlintAndBow(plugin).load()
        DisarmingBow(plugin).load()
        WitherBow(plugin).load()
        FireyBow(plugin).load()
        SlowBow(plugin).load()
        LevelBow(plugin).load()
        UndeadBow(plugin).load()
        WoodmanBow(plugin).load()
        StarvationBow(plugin).load()
        MultiBow(plugin).load()
        BombBow(plugin).load()
        DropBow(plugin).load()
        AirstrikeBow(plugin).load()
        MagmaticBow(plugin).load()
        AquaticBow(plugin).load()
        PullBow(plugin).load()
        ParalyzeBow(plugin).load()
        ClusterBow(plugin).load()
        AirshipBow(plugin).load()
        IronBow(plugin).load()
        CurseBow(plugin).load()
        RoundBow(plugin).load()
        FrozenBow(plugin).load()
        DrillBow(plugin).load()
        MusicBow(plugin).load()
        HomingBow(plugin).load()
        InterdimensionalBow(plugin).load()
        SingularityBow(plugin).load()
        PushyBow(plugin).load()
        RainbowBow(plugin).load()
        LaserBow(plugin).load()
        GrapplingBow(plugin).load()
        BouncyBow(plugin).load()
        MiningBow(plugin).load()
        UpBow(plugin).load()
        ShearBow(plugin).load()
        UndyingBow(plugin).load()
        FireworkBow(plugin).load()
        BridgeBow(plugin).load()
        MeteorBow(plugin).load()
        DraggyBow(plugin).load()
        BabyBow(plugin).load()
        SmokyBow(plugin).load()
        InvincibilityBow(plugin).load()
        BlockyBow(plugin).load()
        AcceleratingBow(plugin).load()
        FarmersBow(plugin).load()
        BowBow(plugin).load()
        MineBow(plugin).load()
        BlasterBow(plugin).load()
        BurstBow(plugin).load()
        ChainBow(plugin).load()
        CuttingBow(plugin).load()
    }

    /**
     * Adds the given ability for this bow type if it is enabled in the configuration file.
     */
    private fun BowAbility.load() {
        if (type.isEnabled(plugin)) {
            bows[type] = this
        }
    }

    /**
     * Unregisters all bows.
     */
    fun unload() = with(plugin) {
        if (bows.isEmpty()) return

        // Unregister event handlers.
        bows.entries.forEach { (bowType, ability) ->
            HandlerList.unregisterAll(ability)
            tasks[bowType]?.let { server.scheduler.cancelTask(it) }
        }
        HandlerList.unregisterAll(this@BowManager)

        bows.clear()
        tasks.clear()
    }

    /**
     * Get the ability implementation for the bow with the given type.
     */
    fun implementation(bowType: BowType) = bows[bowType]

    /**
     * Adds the given bow type when it is enabled in the config.
     */
    private fun addIfEnabled(bowType: BowType, ability: BowAbility) {
        if (plugin.config.getBoolean(bowType.enabledNode)) {
            bows[bowType] = ability
        }
    }

    /**
     * @see implementation
     */
    operator fun get(bowType: BowType) = implementation(bowType)

    override fun iterator() = bows.keys.iterator()

    @EventHandler
    fun playerQuits(event: PlayerQuitEvent) {
        val player = event.player
        bows.values.forEach { it.removeFromCostRequirementsCache(player) }
    }
}