package nl.sugcube.dirtyarrows.effect

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import org.bukkit.Location
import org.bukkit.entity.Fireball
import org.bukkit.entity.Player
import org.bukkit.entity.WitherSkull
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityExplodeEvent

/**
 * Prevents explosions (e.g. from wither skulls/fire charges) in exploded regions.
 *
 * @author SugarCaney
 */
open class ExplosionProtection(private val plugin: DirtyArrows) : Listener {

    /**
     * Ability implementation for the wither bow, or `null` when the wither bow is not available.
     */
    private val witherAbility: BowAbility?
        get() = plugin.bowManager[DefaultBow.WITHER]

    /**
     * Ability implementation for the firey bow, or `null` when the firey bow is not available.
     */
    private val fireyAbility: BowAbility?
        get() = plugin.bowManager[DefaultBow.FIREY]

    @EventHandler
    fun preventExplosions(event: EntityExplodeEvent) = when (event.entity) {
        is WitherSkull -> event.preventWitherExplosion()
        is Fireball -> event.preventFireChargeExplosion()
        else -> Unit
    }

    /**
     * Prevents wither skulls from exploding in protected regions, and refunds the costs.
     */
    private fun EntityExplodeEvent.preventWitherExplosion() {
        val witherAbility = witherAbility ?: return
        if (location.isInProtectedRegion(margin = witherAbility.protectionRange).not()) return

        isCancelled = true

        val skull = entity as WitherSkull
        val shooter = skull.shooter as? Player ?: return
        shooter.sendMessage(plugin.broadcast.witherSkulls())
    }

    /**
     * Prevents fire charges from exploding in protected regions, and refunds the costs.
     */
    private fun EntityExplodeEvent.preventFireChargeExplosion() {
        val fireyAbility = fireyAbility ?: return
        if (location.isInProtectedRegion(margin = fireyAbility.protectionRange).not()) return

        isCancelled = true

        val fireball = entity as Fireball
        val shooter = fireball.shooter as? Player ?: return
        shooter.sendMessage(plugin.broadcast.fireballs())
    }

    /**
     * Checks if the given location is in a protected region.
     *
     * @param margin
     *          How much extra space around the regions must still count as protected.
     */
    private fun Location.isInProtectedRegion(margin: Double): Boolean {
        return plugin.regionManager.isWithinARegionMargin(this, margin) != null
    }
}