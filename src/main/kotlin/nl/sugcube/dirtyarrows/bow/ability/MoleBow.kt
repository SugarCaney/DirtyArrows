package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.centre
import org.bukkit.Effect
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.AbstractArrow.PickupStatus
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.util.Vector

/**
 * Arrows burrow underground and home in on targets.
 *
 * @author SugarCaney
 */
open class MoleBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.MOLE,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = false,
        removeArrow = false,
        description = "Arrows burrow underground looking for targets."
) {

    /**
     * How many blocks/second underground arrows travel.
     */
    val undergroundSpeed = config.getDouble("$node.underground-speed")

    /**
     * How many ticks to continue straight underground before surfacing.
     */
    val burrowTime = config.getInt("$node.burrow-time")

    /**
     * The maximum amount of ticks the arrow can dig before it destroys itself.
     */
    val maxLifespan = config.getInt("$node.max-lifespan")

    private val moleArrows = HashSet<MoleArrow>()

    init {
        check(undergroundSpeed > 0) { "$node.underground-speed must be greater than 0, got <$undergroundSpeed>" }
        check(burrowTime >= 0) { "$node.burrow-time cannot be negative, got <$burrowTime>" }
        check(maxLifespan >= 0) { "$node.max-lifespan cannot be negative, got <$maxLifespan>" }
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        event.hitBlock ?: return

        moleArrows += MoleArrow(
            undergroundSpeed,
            burrowTime,
            shooter = player,
            location = arrow.location.clone(),
            velocity = arrow.velocity.clone(),
            baseDamage = arrow.damage,
            pickupStatus = arrow.pickupStatus,
            knockbackStrength = arrow.knockbackStrength,
            fireTicks = arrow.fireTicks,
            critical = arrow.isCritical
        )

        unregisterArrow(arrow)
        arrow.remove()
    }

    override fun effect() {
        moleArrows.forEach {
            it.tick()

            if (it.location.block.type.isSolid.not() && it.isInBurrowMode.not()) {
                it.shoot()
            }
             if (it.age > maxLifespan) {
                 it.isDone = true
             }
        }

        moleArrows.removeIf { it.isDone }
    }

    override fun particle(tickNumber: Int) {
        moleArrows.forEach { it.particle(tickNumber) }
    }

    /**
     * @author SugarCaney
     */
    class MoleArrow(

        /**
         * The underground speed of the arrow.
         */
        val speed: Double,

        /**
         * How many ticks to continue straight underground before surfacing.
         */
        val burrowTime: Int,

        val shooter: LivingEntity,
        val location: Location,
        val velocity: Vector,
        val baseDamage: Double,
        val pickupStatus: PickupStatus,
        val knockbackStrength: Int,
        val fireTicks: Int,
        val critical: Boolean
    ) {

        var isDone = false
        var target: LivingEntity? = null
        var age = 0

        val isInBurrowMode: Boolean
            get() = age < burrowTime

        fun tick() {
            if (age++ >= burrowTime) {
                moveToTarget()
            }
            else burrow()
        }

        fun moveToTarget() {
            updateTarget()

            val direction = target?.centre?.toVector()?.subtract(location.toVector()) ?: velocity.clone()
            location.add(direction.normalize().multiply(speed / 20.0))
        }

        fun burrow() {
            location.add(velocity.clone().normalize().multiply(1.0 / 20.0 * speed))
        }

        fun shoot() {
            val direction = target?.centre?.toVector()?.add(Vector(0.0, 1.0, 0.0))?.subtract(location.toVector()) ?: velocity.clone()

            location.world!!.spawn(location, Arrow::class.java).apply {
                shooter = this@MoleArrow.shooter
                velocity = direction.normalize().multiply(this@MoleArrow.velocity.length())
                damage = this@MoleArrow.baseDamage
                pickupStatus = this@MoleArrow.pickupStatus
                knockbackStrength = this@MoleArrow.knockbackStrength
                fireTicks = this@MoleArrow.fireTicks
                isCritical = this@MoleArrow.critical
            }

            isDone = true
        }

        fun updateTarget() {
            target = location.world!!.livingEntities.asSequence()
                .filter { it != shooter }
                .minByOrNull { it.centre.distance(location) }
        }

        fun particle(tickNumber: Int) {
            if (tickNumber % 4 != 0) return

            val blockType = location.block.type
            if (blockType != Material.AIR) {
                location.world!!.playEffect(location, Effect.STEP_SOUND, blockType)
            }
        }
    }
}