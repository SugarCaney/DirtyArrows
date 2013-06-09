package nl.SugCube.DirtyArrows;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

public class Swarm {

	public static void doSwarm(World world, Location loc) {
		Location loc1 = new Location(world, loc.getX(), loc.getY(), loc.getZ());
		for (int i = -3; i <= 3; i++) {
			loc1.setZ(loc.getZ() + 3);
			loc1.setX(loc.getX() + i);
			loc1.setY(world.getHighestBlockYAt(loc1));
			world.spawnEntity(loc1, EntityType.ZOMBIE);
			world.playEffect(loc1, Effect.MOBSPAWNER_FLAMES, 0);
		}
		Location loc2 = new Location(world, loc.getX(), loc.getY(), loc.getZ());
		for (int i = -3; i <= 3; i++) {
			loc2.setZ(loc.getZ() - 3);
			loc2.setX(loc.getX() + i);
			loc2.setY(world.getHighestBlockYAt(loc2));
			world.spawnEntity(loc2, EntityType.ZOMBIE);
			world.playEffect(loc2, Effect.MOBSPAWNER_FLAMES, 0);
		}
		Location loc3 = new Location(world, loc.getX(), loc.getY(), loc.getZ());
		for (int i = -2; i <= 2; i++) {
			loc3.setZ(loc.getZ() + i);
			loc3.setX(loc.getX() - 3);
			loc3.setY(world.getHighestBlockYAt(loc3));
			world.spawnEntity(loc3, EntityType.ZOMBIE);
			world.playEffect(loc3, Effect.MOBSPAWNER_FLAMES, 0);
		}
		Location loc4 = new Location(world, loc.getX(), loc.getY(), loc.getZ());
		for (int i = -2; i <= 2; i++) {
			loc4.setZ(loc.getZ() + i);
			loc4.setX(loc.getX() + 3);
			loc4.setY(world.getHighestBlockYAt(loc4));
			world.spawnEntity(loc4, EntityType.ZOMBIE);
			world.playEffect(loc4, Effect.MOBSPAWNER_FLAMES, 0);
		}
	}
	
}
