package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.*
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin
import kotlin.random.Random

/**
 * Shoots arrows that teleport somewhere around the target. After a certain amount of time
 * the arrows shoot toward the target.
 *
 * @author SugarCaney
 */
open class InterdimensionalBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.INTERDIMENSIONAL,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = true,
        removeArrow = false,
        description = "Warps arrows to targets.",
) {

    /**
     * All arrows that have launched and are active.
     */
    private val launched: MutableSet<LaunchState> = ConcurrentHashMap.newKeySet()

    /**
     * Maximum amount of blocks from the player to search for targets.
     */
    val maximumSearchDistance = config.getInt("$node.maximum-search-distance")

    /**
     * How far around the search spot (in blocks) targets can be found.
     */
    val searchRange = config.getDouble("$node.search-range")

    /**
     * The amount of milliseconds before the arrow shoots.
     */
    val shootDelay = config.getInt("$node.shoot-delay")

    /**
     * The maximum deviation from the shoot delay.
     */
    val shootDelayFuzzing = config.getLong("$node.shoot-delay-fuzzing")

    /**
     * How many blocks from the target the arrows should warp to.
     */
    val spawnDistance = config.getDouble("$node.spawn-distance")

    /**
     * The maximum deviation from the spawn distance.
     */
    val spawnDistanceFuzzing = config.getDouble("$node.spawn-distance-fuzzing")

    /**
     * How many arrows to spawn around the target.
     */
    val arrowCount = config.getInt("$node.arrow-count")

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        arrow.findTarget()?.let { target ->
            val bow = player.bowItem() ?: error("Interdimensional arrow was fired without an interdimensional bow")

            repeat(arrowCount) {
                val location = target.arrowWarpLocation() ?: return@let
                location.world?.playSound(location, Sound.ENTITY_ENDERMAN_TELEPORT, 10f, 1f)
                launched += LaunchState(arrow.velocity.copyOf(), player, target, location, bow = bow)
            }
            arrow.showLaunchEffect(player)

            unregisterArrow(arrow)
            arrow.remove()
        }
    }

    /**
     * Determines the location where the arrow warps to, or `null` when no location could be found.
     */
    private fun LivingEntity.arrowWarpLocation(): Location? {
        val radius = spawnDistance.fuzz(spawnDistanceFuzzing)
        val angle = 2.0 * PI * Random.nextDouble()
        val y = location.y + 3.0.fuzz(0.6)

        // Find a location that is air.
        for (dPhi in 0 until 360 step 15) {
            val dPhiRadians = dPhi.toDouble() / 360.0 * 2 * PI
            val x = cos(angle + dPhiRadians) * radius
            val z = sin(angle + dPhiRadians) * radius

            val candidateLocation = Location(world, location.x + x, y, location.z + z)
            val block = candidateLocation.block
            if (block.type == Material.AIR) {
                return candidateLocation
            }
        }

        // Could not found a suitable spawn location.
        return null
    }

    /**
     * Looks for the targetted entity, `null` when not found.
     */
    private fun Arrow.findTarget(): LivingEntity? {
        val shooterEntity = shooter as? LivingEntity ?: return null
        val direction = shooterEntity.location.direction

        //val searchLocation = entity.location.copyOf()
        for (distance in 1..maximumSearchDistance) {
            val searchLocation = location.copyOf().add(direction.copyOf().normalize().multiply(distance))
            val entities = world.getNearbyEntities(searchLocation, searchRange, searchRange, searchRange)
                    .asSequence()
                    .mapNotNull { it as? LivingEntity }
                    .filter { it != shooterEntity }
                    .toList()

            if (entities.isNotEmpty()) {
                return entities.minByOrNull { it.location.distance(shooterEntity.location) }
            }
        }

        return null
    }

    override fun effect() {
        val now = System.currentTimeMillis()

        launched.removeIf { state ->
            val age = now - state.birthTime
            if (age >= shootDelay) {
                state.shootArrow()
                true
            }
            else false
        }
    }

    /**
     * Shoots the arrow from the warp point to the player.
     */
    private fun LaunchState.shootArrow() {
        // Particles and sounds.
        spawnLocation.copyOf(y = spawnLocation.y - 0.5).showFlameParticle()
        spawnLocation.world?.playSound(spawnLocation, Sound.ENTITY_ARROW_SHOOT, 10f, 1f)

        // Launch arrow.
        val direction = target.location.copyOf().subtract(spawnLocation)
        val speed = velocity.length()
        val arrowVelocity = direction.toVector().normalize().multiply(speed).add(Vector(0.0, 0.25, 0.0))

        spawnLocation.world?.spawnEntity(spawnLocation, EntityType.ARROW)?.apply {
            val arrow = this as Arrow
            val player = this@shootArrow.shooter
            arrow.shooter = player
            arrow.velocity = arrowVelocity
            arrow.applyBowEnchantments(bow)

            if (player is Player && player.gameMode == GameMode.CREATIVE) {
                arrow.pickupStatus = Arrow.PickupStatus.CREATIVE_ONLY
            }

            registerArrow(arrow)
        }
    }

    override fun particle(tickNumber: Int) {
        val now = System.currentTimeMillis()

        launched.forEach { state ->
            val age = now - state.birthTime
            val particleCount = age.toDouble() / shootDelay.toDouble() * 25
            state.spawnLocation.fuzz(0.5).showDustEffect(count = particleCount.roundToInt())

            if (tickNumber % 8 == 0) {
                state.spawnLocation.copyOf(y = state.spawnLocation.y - 0.5).showSmokeParticle()
            }
        }
    }

    /**
     * Executes all effects on launch of the arrow.
     */
    private fun Arrow.showLaunchEffect(player: Player) {
        player.playSound(player.location, Sound.ENTITY_ENDERMAN_TELEPORT, 10f, 1f)
        showDustEffect(count = 20, fuzz = 0.5)
    }

    /**
     * Shows particles around the arrow's location.
     */
    private fun Arrow.showDustEffect(count: Int = 50, fuzz: Double = 0.1) {
        repeat(count) {
            location.add(velocity.normalize().multiply(2.0))
                    .fuzz(fuzz)
                    .showDustEffect(1)
        }
    }

    /**
     * Shows warp dust particles at this location.
     */
    private fun Location.showDustEffect(count: Int = 50) = showColoredDust(
            161.fuzz(25),
            24.fuzz(10),
            163.fuzz(15),
            count
    )

    /**
     * @author SugarCaney
     */
    inner class LaunchState(

            /**
             * The intial velocity of the arrow.
             */
            val velocity: Vector,

            /**
             * The entity that shot the arrow.
             */
            val shooter: LivingEntity,

            /**
             * Who to shoot.
             */
            val target: LivingEntity,

            /**
             * Where the arrow will spawn.
             */
            val spawnLocation: Location,

            /**
             * When the arrow was created (unix timestamp).
             */
            val birthTime: Long = System.currentTimeMillis().fuzz(shootDelayFuzzing),

            /**
             * The bow used to fire the arrow.
             */
            val bow: ItemStack
    )
}