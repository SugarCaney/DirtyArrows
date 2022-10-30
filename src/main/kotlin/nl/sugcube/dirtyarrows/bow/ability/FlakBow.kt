package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.*
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileLaunchEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import kotlin.random.Random

/**
 * Shoots slow invisible projectiles that burst into smaller explosions.
 *
 * @author SugarCaney
 */
open class FlakBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.FLAK,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = false,
        protectionRange = 12.0,
        costRequirements = listOf(ItemStack(Material.GUNPOWDER, 15)),
        description = "Close range exploding mayhem."
) {

    /**
     * How many game ticks after launch before the primary projectiles explode.
     */
    val ticksToPrimaryExplosions = config.getInt("$node.primary.ticks-before-explosion")

    /**
     * The maximum (random) deviation from ticks-before-explosion.
     */
    val ticksToPrimaryExplosionsFuzzing = config.getInt("$node.primary.ticks-before-explosion-fuzzing")

    /**
     * How many game ticks after launch before the secondary projectiles explode.
     */
    val ticksToSecondaryExplosions = config.getInt("$node.secondary.ticks-before-explosion")

    /**
     * The maximum (random) deviation from ticks-before-explosion.
     */
    val ticksToSecondaryExplosionsFuzzing = config.getInt("$node.secondary.ticks-before-explosion-fuzzing")

    /**
     * The power of the primary explosions. TNT is 4.0 for reference.
     */
    val powerPrimary = config.getDouble("$node.primary.power").toFloat()

    /**
     * The power of the secondary explosions. TNT is 4.0 for reference.
     */
    val powerSecondary = config.getDouble("$node.secondary.power").toFloat()

    /**
     * Whether primary explosions can break blocks or not.
     */
    val breakBlocksPrimary = config.getBoolean("$node.primary.break-blocks")

    /**
     * Whether secondary explosions can break blocks or not.
     */
    val breakBlocksSecondary = config.getBoolean("$node.secondary.break-blocks")

    /**
     * Whether primary explosions can set blocks on fire or not.
     */
    val setOnFirePrimary = config.getBoolean("$node.primary.set-on-fire")

    /**
     * Whether secondary explosions can set blocks on fire or not.
     */
    val setOnFireSecondary = config.getBoolean("$node.secondary.set-on-fire")

    /**
     * How many projectiles to spawn on bow launch.
     */
    val projectileCountPrimary = config.getInt("$node.primary.projectile-count")

    /**
     * How many secondary projectiles to spawn when a primary projectile bursts.
     */
    val projectileCountSecondary = config.getInt("$node.secondary.projectile-count")

    /**
     * How many blocks the primary projectiles travel per second.
     */
    val projectileSpeedPrimary = config.getDouble("$node.primary.projectile-speed")

    /**
     * The maximum (random) deviation from the primary projectile speed.
     */
    val projectileSpeedFuzzingPrimary = config.getDouble("$node.primary.projectile-speed-fuzzing")

    /**
     * How many blocks the secondary projectiles travel per second.
     */
    val projectileSpeedSecondary = config.getDouble("$node.secondary.projectile-speed")

    /**
     * The maximum (random) deviation from the secondary projectile speed.
     */
    val projectileSpeedFuzzingSecondary = config.getDouble("$node.secondary.projectile-speed-fuzzing")

    /**
     * How strongly the primary projectiles must spread.
     * Higher = more spread, 0.0 = no spread.
     */
    val spread = config.getDouble("$node.spread")

    private val shells = ArrayList<Shell>(projectileCountPrimary * projectileCountSecondary * 4)

    override fun launch(player: Player, arrow: Arrow, event: ProjectileLaunchEvent) {
        repeat(projectileCountPrimary) {
            arrow.location.launchPrimaryProjectile(arrow.velocity.normalize(), player)
        }

        arrow.location.clone().add(0.0, -0.5, 0.0).add(arrow.velocity.clone().normalize().multiply(0.4)).showFlameParticle()
        arrow.world.playSound(arrow.location, Sound.ENTITY_GHAST_SHOOT, 1f, 0f)
        arrow.world.playSound(arrow.location, Sound.ENTITY_ITEM_BREAK, 1f, 0f)

        unregisterArrow(arrow)
        arrow.scheduleRemoval(plugin)
    }

    override fun effect() {
        // Update all shells.
        shells.forEach { it.tick() }

        // When time is up for projectiles, create primary/secondary explosion.
        shells.asSequence()
            .filter { it.canExplode }
            .forEach {
                it.explode()

                if (it.type == ShellType.PRIMARY) {
                    plugin.scheduleDelayed(0L) { it.burst() }
                }
            }

        // Remove exploded projectiles.
        shells.removeIf { it.canExplode }
    }

    override fun particle(tickNumber: Int) {
        //shells.forEach { it.location.showColoredDust(Color.RED, 1) }
    }

    private fun Location.launchPrimaryProjectile(direction: Vector, shooter: LivingEntity? = null) {
        shells += Shell(
            ShellType.PRIMARY,
            Vector(
                direction.x.fuzz(spread),
                direction.y.fuzz(spread),
                direction.z.fuzz(spread)
            ).normalize(),
            projectileSpeedPrimary.fuzz(projectileSpeedFuzzingPrimary),
            ticksToPrimaryExplosions.fuzz(ticksToPrimaryExplosionsFuzzing),
            clone(),
            shooter
        )
    }

    private fun Shell.burst() = repeat(projectileCountSecondary) {
        shells += Shell(
            ShellType.SECONDARY,
            Vector(
                Random.nextDouble(-1.0, 1.0),
                Random.nextDouble(-1.0, 1.0),
                Random.nextDouble(-1.0, 1.0),
            ).normalize(),
            projectileSpeedSecondary.fuzz(projectileSpeedFuzzingSecondary),
            ticksToSecondaryExplosions.fuzz(ticksToSecondaryExplosionsFuzzing),
            location.clone(),
            shooter = shooter
        )
    }

    private fun Location.explodePrimary() = createExplosion(powerPrimary, setOnFirePrimary, breakBlocksPrimary)

    private fun Location.explodeSecondary() = createExplosion(powerSecondary, setOnFireSecondary, breakBlocksSecondary)

    /**
     * @author SugarCaney
     */
    inner class Shell(

        val type: ShellType,

        /**
         * Normalised direction vector.
         */
        val direction: Vector,

        /**
         * Travel speed in blocks per second.
         */
        val speed: Double,

        /**
         * How many ticks the shell can fly before exploding.
         */
        val maxAge: Int,

        /**
         * The current location of the shell.
         */
        val location: Location,

        /**
         * Who shot the shell.
         */
        val shooter: LivingEntity? = null
    ) {

        /**
         * How many ticks old this shell is.
         */
        var age = 0
            private set

        /**
         * Whether the shell is eligible to explode.
         */
        val canExplode: Boolean
            get() = age >= maxAge

        fun tick() {
            age++
            updateLocation()
        }

        private fun updateLocation() {
            if (location.block.type.isSolid) return

            location.x = location.x + direction.x * speed / 20.0
            location.y = location.y + direction.y * speed / 20.0
            location.z = location.z + direction.z * speed / 20.0
        }

        fun explode() {
            if (location.isInProtectedRegion(shooter, false)) return

            when (type) {
                ShellType.PRIMARY -> location.explodePrimary()
                ShellType.SECONDARY -> location.explodeSecondary()
            }
        }
    }

    /**
     * @author SugarCaney
     */
    enum class ShellType {
        PRIMARY,
        SECONDARY;
    }
}