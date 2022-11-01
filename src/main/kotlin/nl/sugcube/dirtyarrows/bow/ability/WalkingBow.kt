package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.Broadcast
import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.*
import org.bukkit.Color
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Arrow
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.ProjectileHitEvent
import org.bukkit.inventory.ItemStack

/**
 * Shoots bows that walk around and shoot living entities.
 *
 * @author SugarCaney
 */
open class WalkingBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.WALKING,
        handleEveryNTicks = 1,
        description = "Shoot walking bows."
) {

    /**
     * How fast the arrows from the sentient bow must be shot.
     */
    val launchVelocity: Double = config.getDouble("$node.launch-velocity")

    /**
     * How many game ticks must be between each shot.
     */
    val shotCooldown: Int = config.getInt("$node.shot-cooldown")

    /**
     * How many arrows the walking bow carries.
     */
    val arrowCount: Int = config.getInt("$node.arrow-count")

    /**
     * How powerful the explosion must be when the magazine is empty.
     */
    val explosionPower = config.getDouble("$node.explosion-power").toFloat()

    /**
     * Whether the final explosion can set blocks on fire.
     */
    val setOnFire: Boolean = config.getBoolean("$node.set-on-fire")

    /**
     * Whether the final explosion can break blocks.
     */
    val breakBlocks: Boolean = config.getBoolean("$node.break-blocks")

    /**
     * The maximum amount of ticks a walking bow can live before they get removed.
     */
    val lifespan: Int = config.getInt("$node.lifespan")

    /**
     * All sentient walking bows.
     */
    private val sentientBows = ArrayList<SentientBow>()

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val bow = player.bowItem() ?: error("Invalid bow item for walking bow.")
        if (player.checkAndRemoveAmmo(bow).not()) return

        repeat(arrowCount) {
            bow.subtractDurability(player)
        }

        val explosion = if (explosionPower > 0.0) {
            ExplosionData(explosionPower, breakBlocks, setOnFire)
        }
        else null

        val zombie = arrow.location.spawn(Zombie::class)?.apply {
            setBaby()
            canPickupItems = false
            isSilent = true
            noDamageTicks = Int.MAX_VALUE
            equipment?.helmet = ItemStack(Material.BOW, 1).apply {
                addUnsafeEnchantment(Enchantment.DURABILITY, 10)
            }
            isInvisible = true
            scoreboardTags.add(ENTITY_TAG)
        } as Zombie

        sentientBows += SentientBow(
            shooter = player,
            zombie = zombie,
            bowItem = bow.clone(),
            arrowCount = arrowCount,
            launchVelocity = launchVelocity,
            explosionData = explosion
        )
    }

    /**
     * Checks whether the player has enough ammo and removes it from the inventory if applicable.
     * Also shows error message.
     *
     * @return `true` when the player has enough ammo, `false` when the player does not have enough ammo.
     */
    private fun Player.checkAndRemoveAmmo(bow: ItemStack): Boolean {
        if (gameMode == GameMode.CREATIVE) return true

        val hasInfinity = bow.containsEnchantment(Enchantment.ARROW_INFINITE)
        val itemCount = if (hasInfinity) 1 else arrowCount
        val hasEnough = inventory.containsAtLeast(ItemStack(Material.ARROW), itemCount)

        if (hasEnough.not()) {
            sendMessage(Broadcast.NOT_ENOUGH_RESOURCES.format("${Material.ARROW.name.toLowerCase()} (x${itemCount})"))
            return false
        }

        if (hasInfinity.not()) {
            inventory.removeIncludingData(ItemStack(Material.ARROW, itemCount))
        }

        return true
    }

    override fun effect() {
        sentientBows.forEach {
            it.tick()
            it.updateTarget()
            it.shoot()

            if (it.age > lifespan || (it.arrowCount <= 0 && it.cooldownTicks <= 0)) {
                it.done()
            }
        }

        sentientBows.removeIf { it.isDone }
    }

    override fun particle(tickNumber: Int) {
        sentientBows.forEach {
            it.zombie.centre.showColoredDust(Color.fromRGB(165, 113, 24), 1)
        }

        if (tickNumber % 40 == 0) {
           cleanupZombies()
        }
    }

    /**
     * Removes all zombies on the server that used to be a walking bow but are somehow not registered.
     */
    fun cleanupZombies() {
        plugin.server.worlds.forEach { world ->
            world.entities.forEach { entity ->
                if (ENTITY_TAG in entity.scoreboardTags && sentientBows.none { it.zombie == entity }) {
                    plugin.scheduleDelayed(0) { entity.remove() }
                }
            }
        }
    }

    @EventHandler
    fun disarmBowByProjectile(event: ProjectileHitEvent) {
        val shotZombie = event.hitEntity ?: return
        if (shotZombie !is Zombie) return

        val sentientBow = sentientBows.firstOrNull { it.zombie == shotZombie } ?: return
        if (sentientBow.shooter == event.entity.shooter) return
        sentientBow.done()
    }

    @EventHandler
    fun disableZombieDrops(event: EntityDeathEvent) {
        if (ENTITY_TAG in event.entity.scoreboardTags) {
            event.drops.clear()
        }
    }

    /**
     * @author SugarCaney
     */
    inner class SentientBow(

        /**
         * Who shot the bow.
         */
        val shooter: LivingEntity,

        /**
         * The zombie that is used to make the bow walk.
         */
        val zombie: Zombie,

        /**
         * With which bow item the sentient bow has been spawned.
         */
        val bowItem: ItemStack,

        /**
         * How many arrows the sentient bow carries.
         */
        var arrowCount: Int,

        /**
         * How fast the arrows from the sentient bow must be shot.
         */
        val launchVelocity: Double = 2.8,

        /**
         * How many game ticks must be between each game shot.
         */
        val shotCooldown: Int = 15,

        /**
         * From how far the sentient bow starts shooting targets.
         */
        val maxShotDistance: Double = 16.0,

        /**
         * Properties of the final explosion, null for no explosion.
         */
        val explosionData: ExplosionData? = null
    ) {

        /**
         * How many ticks have passed, i.e. the age of the bow.
         */
        var age: Int = 0
            private set

        /**
         * Whether the bow is done with its task and can be removed.
         */
        var isDone: Boolean = false
            private set

        /**
         * How many ticks are left before the zombie can shoot.
         * Numbers <= 0 mean that the zombie can shoot.
         */
        var cooldownTicks: Int = shotCooldown
            private set

        fun tick() {
            age++
            cooldownTicks--

            zombie.fireTicks = 0
        }

        /**
         * Shoots its target if possible.
         */
        fun shoot() {
            val target = zombie.target ?: return
            if (arrowCount <= 0 || cooldownTicks > 0) return
            if (target.location.distance(zombie.location) > maxShotDistance) return

            zombie.centre.launchArrowAt(target.centre, launchVelocity, shooter, bowItem)
            arrowCount--
            cooldownTicks = shotCooldown
        }

        /**
         * Checks if the bow is done, if so explode.
         */
        fun done() {
            if (isDone) return
            isDone = true

            explosionData?.let {
                zombie.location.createExplosion(it.power, it.setOnFire, it.breakBlocks)
            }

            zombie.remove()
            dropRemainingArrows()
        }

        /**
         * Drops half of the arrows that have not been shot yet.
         */
        fun dropRemainingArrows() {
            if (arrowCount <= 0) return
            // When shot with infinity: do not drop the arrows.
            if (bowItem.containsEnchantment(Enchantment.ARROW_INFINITE)) return

            repeat(arrowCount / 2) {
                zombie.location.dropItem(ItemStack(Material.ARROW, 1))
            }
        }

        /**
         * Re-determine the new target.
         */
        fun updateTarget() {
            val target = findTarget()

            if (target == null || target.location.distance(zombie.location) < 3.0) {
                zombie.setAI(false)
            }
            else {
                zombie.setAI(true)
                zombie.target = target
            }
        }

        fun findTarget(): LivingEntity? = zombie.world.livingEntities.asSequence()
            .filter { it != shooter /* don't shoot the original shooter */ && it != zombie /* don't target the same bow */ }
            .filter { entity -> /* don't target friendly walking bows */
                sentientBows.firstOrNull { it.shooter == shooter && it.zombie == entity } == null
            }
            .minByOrNull { it.location.distance(zombie.location) }
    }

    companion object {

        const val ENTITY_TAG = "nl.sugcube.dirtyarrows.bow.ability.walking"
    }
}