package nl.sugcube.dirtyarrows.util

import org.bukkit.Color

/**
 * @author SugarCaney
 */
open class ColourScheme(vararg val colours: Color) : List<Color> by colours.toList() {

    companion object {

        val TRICOLORE = ColourScheme(
                Color.fromRGB(174, 28, 40),
                Color.fromRGB(255, 255, 255),
                Color.fromRGB(33, 70, 139),
        )

        val RAINBOW = ColourScheme(
                Color.fromRGB(255, 0, 0),
                Color.fromRGB(255, 140, 0),
                Color.fromRGB(255, 238, 0),
                Color.fromRGB(77, 233, 76),
                Color.fromRGB(55, 131, 255),
                Color.fromRGB(72, 21, 170),
        )

        val ORANGE_YELLOW = ColourScheme(
                Color.fromRGB(245, 128, 37),
                Color.fromRGB(245, 152, 48),
                Color.fromRGB(245, 176, 59),
                Color.fromRGB(245, 200, 69),
                Color.fromRGB(245, 224, 80),
        )

        val TRANS_RIGHTS = ColourScheme(
                Color.fromRGB(85, 205, 252),
                Color.fromRGB(255, 255, 255),
                Color.fromRGB(247, 168, 184),
        )

        val LOVE_PASTEL = ColourScheme(
                Color.fromRGB(255, 240, 243),
                Color.fromRGB(254, 225, 230),
                Color.fromRGB(206, 204, 228),
                Color.fromRGB(230, 247, 241),
                Color.fromRGB(255, 251, 242),
        )

        val REAL_SKIN_TONES = ColourScheme(
                Color.fromRGB(141, 85, 36),
                Color.fromRGB(198, 134, 66),
                Color.fromRGB(224, 172, 105),
                Color.fromRGB(241, 194, 125),
                Color.fromRGB(255, 219, 172),
        )

        val LEAF_IN_FALL = ColourScheme(
                Color.fromRGB(186, 70, 52),
                Color.fromRGB(216, 92, 78),
                Color.fromRGB(234, 162, 80),
                Color.fromRGB(245, 221, 139),
                Color.fromRGB(206, 194, 24),
                Color.fromRGB(95, 120, 24),
        )

        val OCEAN = ColourScheme(
                Color.fromRGB(7, 96, 148),
                Color.fromRGB(10, 124, 190),
                Color.fromRGB(75, 199, 207),
                Color.fromRGB(121, 217, 210),
                Color.fromRGB(32, 178, 170),
        )

        val EUROPE = ColourScheme(
                Color.fromRGB(0, 51, 153),
                Color.fromRGB(0, 51, 153),
                Color.fromRGB(255, 204, 0),
                Color.fromRGB(0, 51, 153),
                Color.fromRGB(0, 51, 153),
        )

        val ACE = ColourScheme(
                Color.fromRGB(0, 0, 0),
                Color.fromRGB(164, 164, 164),
                Color.fromRGB(255, 255, 255),
                Color.fromRGB(129, 0, 129),
        )

        val LIME = ColourScheme(
                Color.fromRGB(77, 138, 18),
                Color.fromRGB(130, 183, 42),
                Color.fromRGB(194, 224, 106),
                Color.fromRGB(227, 231, 138),
                Color.fromRGB(253, 246, 190),
        )

        val SCARLET = ColourScheme(
                Color.fromRGB(255, 247, 240),
                Color.fromRGB(255, 61, 61),
                Color.fromRGB(198, 47, 47),
                Color.fromRGB(165, 39, 39),
                Color.fromRGB(127, 15, 11),
        )

        val DEFAULT_SCHEMES = listOf(
                TRICOLORE, RAINBOW, ORANGE_YELLOW, TRANS_RIGHTS, LOVE_PASTEL, REAL_SKIN_TONES, LEAF_IN_FALL,
                OCEAN, EUROPE, ACE, LIME, SCARLET
        )
    }
}