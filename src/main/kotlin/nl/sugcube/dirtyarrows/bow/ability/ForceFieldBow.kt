package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.*
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Tameable
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.cos
import kotlin.math.sin

/**
 * Pulls in all close entities and damages from suffocation.
 *
 * @author SugarCaney
 */
open class ForceFieldBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.FORCEFIELD,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = false,
        protectionRange = 20.0,
        removeArrow = true,
        description = "Creates a force field that pushes entities away.",
        costRequirements = listOf(ItemStack(Material.REDSTONE, 5), ItemStack(Material.FEATHER, 3))
) {

    /**
     * How many game ticks the force field stays active.
     */
    val duration = config.getInt("$node.duration")

    /**
     * How many game ticks to add to the duration of the force field per level of unbreaking on the bow.
     */
    val extraDurationPerUnbreakingLevel = config.getInt("$node.extra-duration-per-unbreaking-level")

    /**
     * How many blocks from the centre of the force field to the edge.
     */
    val radius = config.getDouble("$node.radius")

    /**
     * The push velocity outward at the centre.
     */
    val pushPower = config.getDouble("$node.push-power")

    private val forceFields = HashSet<ForceField>()

    init {
        check(duration > 0.0) { "$node.duration must be positive, got <$radius>" }
        check(radius > 0.0) { "$node.radius must be positive, got <$radius>" }
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val unbreakingLevel = player.bowItem()?.getEnchantmentLevel(Enchantment.DURABILITY) ?: 0

        forceFields += ForceField(
            owner = player,
            location = arrow.location,
            radius = radius,
            lifeTime = duration + unbreakingLevel * extraDurationPerUnbreakingLevel,
            pushPower = pushPower
        )
    }

    override fun effect() {
        forceFields.forEach { it.tick() }
        forceFields.removeIf { it.isDone }
    }

    override fun particle(tickNumber: Int) {
        if (tickNumber % 6 != 0) return

        forceFields.forEach { it.particle()  }
    }

    /**
     * @author SugarCaney
     */
    inner class ForceField(

        /**
         * Who has created the force field.
         */
        val owner: LivingEntity,

        /**
         * The centre of the force field.
         */
        val location: Location,

        /**
         * The radius of the force field in blocks.
         */
        val radius: Double,

        /**
         * How old the force field can be in ticks.
         */
        val lifeTime: Int,

        /**
         * The push velocity length.
         */
        val pushPower: Double
    ) {

        var isDone = false
        var age = 0
            private set

        fun tick() {
            if (age++ > lifeTime) {
                isDone = true
            }

            pushEntities()
        }

        fun pushEntities() = location.nearbyLivingEntities(radius).asSequence()
            .filter { it != owner }
            .filter { it !is Tameable || it.owner != owner }
            .forEach { entity ->
                val pushDirection = entity.location.toVector()
                    .subtract(location.toVector())
                    .normalize()
                    .multiply(pushPower)

                entity.velocity = entity.velocity.add(pushDirection)
            }

        fun particle() {
            fun showRing(dxf: (Double) -> Double, dyf: (Double) -> Double, dzf: (Double) -> Double, count: Int = 50) {
                repeat(count) {
                    val dx = radius * dxf(it * Math.PI * 2 / 50)
                    val dy = radius * dyf(it * Math.PI * 2 / 50)
                    val dz = radius * dzf(it * Math.PI * 2 / 50)

                    location.copyOf(
                        x = location.x + dx,
                        y = location.y + dy,
                        z = location.z + dz,
                    ).showColoredDust(COLOURS.random(), 1)
                }
            }

            showRing(::cos, ::sin, ::sin)
            showRing(::cos, ::cos, ::sin)
            showRing(::cos, ::sin, ::cos)

            location.showSmokeParticle()
        }
    }

    companion object {

        val PURPLES = (1..10).map { Color.fromRGB(110.fuzz(10), 18, 124.fuzz(10)) }
        val BLUES = (1..10).map { Color.fromRGB(100.fuzz(10), 100.fuzz(10), 224.fuzz(2)) }
        val COLOURS = PURPLES + BLUES
    }
}