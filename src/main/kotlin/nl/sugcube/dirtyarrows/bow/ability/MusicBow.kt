package nl.sugcube.dirtyarrows.bow.ability

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.BowAbility
import nl.sugcube.dirtyarrows.bow.DefaultBow
import nl.sugcube.dirtyarrows.util.fuzz
import nl.sugcube.dirtyarrows.util.itemInMainHand
import nl.sugcube.dirtyarrows.util.itemInOffHand
import nl.sugcube.dirtyarrows.util.showMusicNoteParticle
import org.bukkit.Effect
import org.bukkit.Material
import org.bukkit.entity.Arrow
import org.bukkit.entity.Player
import org.bukkit.event.entity.ProjectileHitEvent

/**
 * Plays music on impact.
 *
 * @author SugarCaney
 */
open class MusicBow(plugin: DirtyArrows) : BowAbility(
        plugin = plugin,
        type = DefaultBow.MUSIC,
        canShootInProtectedRegions = false,
        removeArrow = true,
        description = "Plays a music disc at the location of impact."
) {

    override fun land(arrow: Arrow, player: Player, event: ProjectileHitEvent) {
        val record = player.record() ?: run {
            player.sendMessage(plugin.broadcast.noRecord())
            return
        }

        arrow.location.world?.playEffect(arrow.location, Effect.RECORD_PLAY, record)
    }

    override fun particle(tickNumber: Int) = arrows.forEach {
        it.location.fuzz(0.15).showMusicNoteParticle()
    }

    /**
     * Get the first record in the player's inventory, or `null` when no record is found.
     */
    private fun Player.record(): Material? {
        if (itemInMainHand.type.isRecord) return itemInMainHand.type
        if (itemInOffHand.type.isRecord) return itemInOffHand.type
        return inventory.firstOrNull { it?.type?.isRecord == true }?.type
    }
}