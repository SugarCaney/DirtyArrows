package nl.sugcube.dirtyarrows.util;

import org.bukkit.util.Vector;

import java.util.Random;

public class Util {

    /**
     * TODO: Move to cluster bow class class.
     */
    public static Vector ranCluster(Random ran) {
		Vector v = new Vector();
		v.setX(0.3 - 0.3 + ran.nextDouble() * 0.147 * 2);
		v.setY(0.7 + ran.nextDouble() * 0.35);
		v.setZ(0.3 - 0.3 + ran.nextDouble() * 0.147 * 2);
		return v;
	}
}
