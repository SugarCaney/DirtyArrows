package nl.sugcube.dirtyarrows.bow

import nl.sugcube.dirtyarrows.util.applyColours
import org.bukkit.configuration.file.FileConfiguration

/**
 * @author SugarCaney
 */
enum class Bow(

    /**
     * The node for the bow in the configuration.
     */
    val node: String
) {

    EXPLODING("exploding"),
    LIGHTNING("lightning"),
    CLUCKY("clucky"),
    ENDER("ender"),
    OAK("oak"),
    SPRUCE("spruce"),
    BIRCH("birch"),
    JUNGLE("jungle"),
    BATTY("batty"),
    NUCLEAR("nuclear"),
    ENLIGHTENED("enlightened"),
    RANGED("ranged"),
    MACHINE("machine"),
    POISONOUS("poisonous"),
    DISORIENTING("disorienting"),
    SWAP("swap"),
    DRAINING("draining"),
    FLINT("flintand"),
    DISARMING("disarming"),
    WITHER("wither"),
    FIREY("firey"),
    SLOW("slow"),
    LEVEL("level"),
    UNDEAD("undead"),
    WOODMAN("woodman"),
    STARVATION("starvation"),
    MULTI("multi"),
    BOMB("bomb"),
    DROP("drop"),
    AIRSTRIKE("airstrike"),
    MAGMATIC("magmatic"),
    AQUATIC("aquatic"),
    PULL("pull"),
    PARALYZE("paralyze"),
    ACACIA("acacia"),
    DARK_OAK("darkoak"),
    CLUSTER("cluster"),
    AIRSHIP("airship"),
    IRON("iron"),
    CURSE("curse"),
    ROUND("round"),
    FROZEN("frozen"),
    ;

    /**
     * Numerical identifier of the bow type.
     */
    val id: Int
        get() = ordinal + 1

    /**
     * The configuration node for the name of the bow.
     */
    val nameNode: String
        get() = "$node.name"

    /**
     * The configuration node for the anvil level cost of the bow.
     */
    val levelsNode: String
        get() = "$node.levels"

    companion object {

        /**
         * Get all bows in order of definintion.
         */
        @JvmStatic
        val ALL = values().toList()

        /**
         * Get all node names.
         */
        @JvmStatic
        val ALL_NODES = ALL.map { it.node }.toSet()

        /**
         * Get the bow with the given ID (1-indexed), `null` when there is no bow with id.
         */
        @JvmStatic
        fun bowById(id: Int): Bow? = ALL.getOrNull(id - 1)

        /**
         * Get the bow with the given configuration node, `null` whe nnot exists.
         */
        @JvmStatic
        fun bowByNode(node: String): Bow? = ALL.firstOrNull() { it.node == node }

        /**
         * Turns the given string into a bow object.
         * Looks for node names or id numbers.
         *
         * @return The corresponding Bow or `null` when no bow could be found.
         */
        @JvmStatic
        fun parseBow(input: String): Bow? {
            // When numeric.
            return input.toIntOrNull()?.let { idNumber ->
                bowById(idNumber)
            } ?: bowByNode(input.toLowerCase())
        }

        /**
         * Get the bow that has the given `itemName` configured.
         */
        @JvmStatic
        fun bowByItemName(itemName: String, config: FileConfiguration): Bow? = ALL.firstOrNull {
            val candidateName = config.getString(it.nameNode).applyColours()
            itemName.applyColours() == candidateName
        }
    }
}