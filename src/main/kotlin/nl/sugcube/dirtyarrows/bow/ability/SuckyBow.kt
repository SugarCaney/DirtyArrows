package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.copyOf
import nl.sugcube.dirtyarrows.util.fuzz
import nl.sugcube.dirtyarrows.util.nearbyEntities
import nl.sugcube.dirtyarrows.util.showColoredDust
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.Sound
import org.bukkit.entity.Arrow
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent
import kotlin.math.min

/**
 * Sucks in items and adds them to your inventory.
 *
 * @author SugarCaney
 */
open class SuckyBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.SUCKY,
        handleEveryNTicks = 1,
        canShootInProtectedRegions = false,
        protectionRange = 20.0,
        removeArrow = true,
        description = "Sucks in items and adds them to your inventory.",
) {

    /**
     * How many blocks from the centre of the force field to the edge.
     */
    val radius = config.getDouble("$node.radius")

    private val suckyWuckySuckers = HashSet<SuckyWuckySucker>()

    init {
        check(radius > 0.0) { "$node.radius must be positive, got <$radius>" }
    }

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        suckyWuckySuckers += SuckyWuckySucker(
            owner = player,
            location = arrow.location,
            radius = radius,
            lifeTime = 60,
            pullPower = 0.065
        )
    }

    override fun effect() {
        suckyWuckySuckers.forEach {
            it.tick()

            if (it.isDone) {
                it.addItemsToInventory()
            }
        }

        suckyWuckySuckers.removeIf { it.isDone }
    }

    override fun particle(tickNumber: Int) {
        if (tickNumber % 3 != 0) return

        suckyWuckySuckers.forEach { it.particle() }
    }

    /**
     * @author SugarCaney
     */
    inner class SuckyWuckySucker(

        /**
         * Who shot the arrow.
         */
        val owner: Player,

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
         * The pull velocity length.
         */
        val pullPower: Double
    ) {

        var isDone = false
        var age = 0
            private set

        fun tick() {
            if (age++ > lifeTime) {
                isDone = true
            }

            pullEntities()
        }

        fun pullEntities() = location.nearbyEntities(radius).asSequence()
            .mapNotNull { it as? Item }
            .forEach { item ->
                val pullVector = location.toVector().subtract(item.location.toVector())
                val newLength = min(pullVector.length() / 20.0, pullPower)
                val pullDirection = pullVector.normalize().multiply(newLength)

                item.velocity = item.velocity.add(pullDirection)
            }

        fun particle() {
            location.copyOf(
                x = location.x.fuzz(0.17),
                y = location.y.fuzz(0.17),
                z = location.z.fuzz(0.17),
            ).showColoredDust(Color.BLACK, 1)
        }

        fun addItemsToInventory() = location.nearbyEntities(1.0).asSequence()
            .mapNotNull { it as? Item }
            .forEach { item ->
                owner.inventory.addItem(item.itemStack)
                owner.playSound(owner.location, Sound.ENTITY_ITEM_PICKUP, 1f, 0f)
                item.remove()
            }
    }
}