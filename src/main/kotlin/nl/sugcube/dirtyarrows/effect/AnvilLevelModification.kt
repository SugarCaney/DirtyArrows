package nl.sugcube.dirtyarrows.effect

import nl.sugcube.dirtyarrows.DirtyArrows
import nl.sugcube.dirtyarrows.bow.DefaultBow
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareAnvilEvent
import kotlin.math.max

/**
 * Modifies the level cost of the special bows in anvils.
 *
 * @author SugarCaney
 */
open class AnvilLevelModification(private val plugin: DirtyArrows) : Listener {

    @EventHandler
    fun anvilPreProcessor(event: PrepareAnvilEvent) {
        val anvil = event.inventory
        val result = event.result ?: return
        if (result.type != Material.BOW) return

        val nameCandidate = anvil.renameText
        val bowCandidate = DefaultBow.bowByItemName(nameCandidate, plugin.config) ?: return
        anvil.repairCost = max(1, plugin.config.getInt(bowCandidate.levelsNode))
    }
}