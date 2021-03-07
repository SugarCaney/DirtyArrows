package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.Broadcast
import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.copyOf
import nl.sugcube.dirtyarrows.util.forXYZ
import nl.sugcube.dirtyarrows.util.onSolid
import org.bukkit.Effect
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityInteractEvent
import org.bukkit.event.entity.EntityShootBowEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.min
import kotlin.random.Random

/**
 * Shoots arrows with a cool ability B)
 * - Has a chance to spawn snow layers.
 * - Can freeze water blocks.
 * - Can ice into packed ice.
 * - Freezes entities that get hit.
 *
 * @author SugarCaney
 */
open class FrozenBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.FROZEN,
        handleEveryNTicks = 15,
        canShootInProtectedRegions = false,
        protectionRange = 8.0,
        costRequirements = listOf(ItemStack(Material.SNOW_BALL, 1)),
        description = "Spawns snow/ice and freezes targets."
) {

    /**
     * All entities that are frozen, mapped to the time they were frozen.
     */
    private val frozenEntities = ConcurrentHashMap<Entity, Long>()

    /**
     * How far along the x/z axis the bow can freeze.
     */
    val horizontalRange = config.getInt("$node.horizontal-range")

    /**
     * How far along the y axis the bow can freeze.
     */
    val verticalRange = config.getInt("$node.vertical-range")

    /**
     * Chance for a block to spawn snow.
     */
    val snowChance = config.getDouble("$node.snow-chance")

    /**
     * Chance to spawn an extra snow layer.
     */
    val snowExtraLayerChance = config.getDouble("$node.snow-extra-layer-chance")

    /**
     * Chance for a water block to freeze.
     */
    val freezeChance = config.getDouble("$node.freeze-chance")

    /**
     * Chance for an ice block to turn into packed ice.
     */
    val packedIceChance = config.getDouble("$node.packed-ice-chance")

    /**
     * The amount of milliseconds an entity stays frozen.
     */
    val freezeTime = config.getInt("$node.freeze-time")

    /**
     * How much varience there is in freeze time in milliseconds.
     */
    val freezeTimeFuzzing = config.getInt("$node.freeze-time-fuzzing")

    /**
     * A particle will spawn from shot arrows every N ticks.
     */
    val particleEveryNTicks = config.getInt("$node.particle-every-n-ticks")

    init {
        check(snowChance in 0.0..1.0) { "$node.snow-chance must be between 0 and 1, got <$snowChance>" }
        check(snowExtraLayerChance in 0.0..1.0) { "$node.snow-extra-layer-chance must be between 0 and 1, got <$snowExtraLayerChance>" }
        check(freezeChance in 0.0..1.0) { "$node.freeze-chance must be between 0 and 1, got <$freezeChance>" }
        check(packedIceChance in 0.0..1.0) { "$node.packed-ice-chance must be between 0 and 1, got <$packedIceChance>" }
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        freezeHitEntity(event.hitEntity)
        freezeLandscape(arrow)
    }

    /**
     * Freezes terrain features.
     */
    private fun freezeLandscape(arrow: Arrow) = forXYZ(
            -horizontalRange..horizontalRange,
            -verticalRange..verticalRange,
            -horizontalRange..horizontalRange
    ) { dx, dy, dz ->
        val target = arrow.location.copyOf().add(dx.toDouble(), dy.toDouble(), dz.toDouble())
        val block = target.block

        when (block.type) {
            // Place snow layers.
            Material.AIR -> {
                if (block.onSolid() && block.type == Material.AIR && Random.nextDouble() < snowChance) {
                    block.type = Material.SNOW
                }
            }
            // Increase snow level.
            Material.SNOW -> {
                if (Random.nextDouble() < snowExtraLayerChance) {
                    @Suppress("DEPRECATION") /* Could not find a Snow BlockData variant in 1.11.2 */
                    block.data = min(block.data + 1, 7).toByte()
                }
            }
            // Freeze water.
            Material.STATIONARY_WATER -> {
                if (Random.nextDouble() < freezeChance) {
                    block.type = Material.ICE
                }
            }
            // Deep freeze ice blocks to packed ice.
            Material.ICE -> {
                if (Random.nextDouble() < packedIceChance) {
                    block.type = Material.PACKED_ICE
                }
            }
            else -> Unit
        }
    }

    /**
     * When the arrow hits an entity, freeze it. Does only freeze LivingEntity s.
     *
     * @param target
     *          The entity that has been hit.
     */
    private fun freezeHitEntity(target: Entity?) {
        val entity = target as? LivingEntity ?: return

        // Mark hit entity as frozen.
        frozenEntities[entity] = System.currentTimeMillis() + Random.nextInt(-freezeTimeFuzzing, freezeTimeFuzzing)
        entity.world.playEffect(entity.location.copyOf().add(0.5, 0.5, 0.5), Effect.STEP_SOUND, Material.SNOW_BLOCK)

        if (entity is Player) {
            entity.sendMessage(Broadcast.FROZEN)
        }
        else entity.addPotionEffect(PotionEffect(PotionEffectType.SLOW, 900000, 7, true, true))
    }

    override fun effect() {
        frozenEntities.keys.removeIf { entity ->
            val start = frozenEntities[entity]!!
            val elapsed = System.currentTimeMillis() - start
            val isDone = elapsed >= freezeTime
            if (isDone) {
                when (entity) {
                    is Player -> entity.sendMessage(Broadcast.DEFROSTED)
                    is LivingEntity -> entity.removePotionEffect(PotionEffectType.SLOW)
                }
            }
            isDone
        }
    }

    override fun particle(tickNumber: Int) {
        if (tickNumber % particleEveryNTicks == 0) return

        arrows.forEach {
            it.world.playEffect(it.location.copyOf().add(0.5, 0.5, 0.5), Effect.STEP_SOUND, Material.SNOW_BLOCK)
        }
    }

    @EventHandler
    fun entityInteractionHandler(event: EntityInteractEvent) {
        event.isCancelled = frozenEntities.containsKey(event.entity)
    }

    @EventHandler
    fun entityBowUse(event: EntityShootBowEvent) {
        event.isCancelled = frozenEntities.containsKey(event.entity)
    }

    @EventHandler
    fun playerMoveHandler(event: PlayerMoveEvent) {
        event.isCancelled = frozenEntities.containsKey(event.player)
    }

    @EventHandler
    fun unfreezeOnDeath(event: EntityDeathEvent) {
        frozenEntities.remove(event.entity)
    }
}