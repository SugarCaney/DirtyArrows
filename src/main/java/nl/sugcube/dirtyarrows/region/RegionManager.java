package nl.sugcube.dirtyarrows.region;

import nl.sugcube.dirtyarrows.DirtyArrows;
import nl.sugcube.dirtyarrows.util.DaUtil;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class RegionManager {

	public static DirtyArrows plugin;
	
	private final List<Region> registeredRegions = new ArrayList<>();
	private Location position1;
	private Location position2;
	
	public RegionManager(DirtyArrows da) {
		plugin = da;
	}
	
	/**
	 * Removes a region.
	 * @param name (String) Name of the region to remove.
	 */
	public void removeRegion(String name) {
		for (int i = 0; i < registeredRegions.size(); i++) {
			Region reg = registeredRegions.get(i);
			if (reg.getName().equalsIgnoreCase(name)) {
				registeredRegions.remove(reg);
				plugin.getData().set("regions." + name, null);
				plugin.saveData();
				plugin.reloadData();
				return;
			}
		}
	}
	
	/**
	 * Sets the selected location of position 1 or 2.
	 * @param id (int) 1 for position 1, and 2 for position 2
	 * @param loc (Location) Location to set the selection to.
	 */
	public void setSelection(int id, Location loc) {
		if (id == 1) {
			this.position1 = loc;
		} else {
			this.position2 = loc;
		}
	}
	
	/**
	 * Loads all the regions from the data.yml
	 */
	public void loadRegions() {
		if (!plugin.getData().isSet("regions")) {
			return;
		}
		for (String key : plugin.getData().getConfigurationSection("regions").getKeys(false)) {
			String path = "regions." + key + ".";
			String pos1string = plugin.getData().getString(path + "pos1");
			String pos2string = plugin.getData().getString(path + "pos2");
			Location loc1 = DaUtil.toLocation(pos1string);
			Location loc2 = DaUtil.toLocation(pos2string);
			Region region = new Region(loc1, loc2, key);
			registeredRegions.add(region);
		}
	}
	
	/**
	 * Saves all the regions to the data.yml
	 */
	public boolean saveRegions() {
		boolean error = false;
        for (Region registeredRegion : registeredRegions) {
            try {
                if (registeredRegion == null || registeredRegion.getName() == "") {
                    continue;
                }

                if (registeredRegion.getLocation(1) != null) {
                    plugin.getData().set("regions." + registeredRegion.getName() + ".pos1", DaUtil.toLocationString(registeredRegion.getLocation(1)));
                }

                if (registeredRegion.getLocation(2) != null) {
                    plugin.getData().set("regions." + registeredRegion.getName() + ".pos2", DaUtil.toLocationString(registeredRegion.getLocation(2)));
                }

                plugin.saveData();
            }
            catch (Exception e) {
                error = true;
            }
        }
		plugin.getLogger().info("Could not save locations.");
        return error;
	}
	
	/**
	 * Gives a list of all region-names stored.
	 * @return (StringList)
	 */
	public List<String> getAllNames() {
		List<String> names = new ArrayList<String>();
        for (Region registeredRegion : registeredRegions) {
            names.add(registeredRegion.getName());
        }
		
		return names;
	}
	
	/**
	 * Checks if there already exists a region with the given name.
	 * @param name (String) The region's name to be checked.
	 * @return (boolean) True if it does exist, false if not.
	 */
	public boolean doesExist(String name) {
        for (Region registeredRegion : registeredRegions) {
            if (registeredRegion.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }
		
		return false;
	}
	
	/**
	 * Checks if there already exists a region with the given name.
	 * @param reg (Region) The region to be checked.
	 * @return (boolean) True if it does exist, false if not.
	 */
	public boolean doesExist(Region reg) {
        for (Region registeredRegion : registeredRegions) {
            if (registeredRegion.getName().equalsIgnoreCase(reg.getName())) {
                return true;
            }
        }
		
		return false;
	}
	
	/**
	 * Gets a region by it's name.
	 * @param name (String) name of the Region.
	 * @return (Region) or `null` when there is no region with the given name.
	 */
	public Region regionByName(String name) {
        for (Region registeredRegion : registeredRegions) {
            if (registeredRegion.getName().equalsIgnoreCase(name)) {
                return registeredRegion;
            }
        }
		
		return null;
	}
	
	public Region isWithinAXZMargin(Location loc, int margin) {
        for (Region region : registeredRegions) {
            if (RegionManager.isWithinXZMargin(region, loc, margin)) {
                return region;
            }
        }
		
		return null;
	}
	
	/**
	 * Checks wether the given locations is within one of the
	 * registered regions (with margin).
	 * @param loc (Location)
	 * @param margin (int) expansion of the region.
	 * @return (Region) null if not in a region.
	 */
	public Region isWithinARegionMargin(Location loc, int margin) {
        for (Region region : registeredRegions) {
            if (RegionManager.isWithinRegionMargin(region, loc, margin)) {
                return region;
            }
        }
		
		return null;
	}
	
	/**
	 * Checks wether the given location is within one of the
	 * registered regions.
	 * @param loc (Location)
	 * @return (Region) null if not in a region.
	 */
	public Region isWithinARegion(Location loc) {
        for (Region region : registeredRegions) {
            if (RegionManager.isWithinRegion(region, loc)) {
                return region;
            }
        }
		
		return null;
	}
	
	/**
	 * Creates a region from the positions stored in memory and will
	 * immedeately register the region.
	 * @param registerName (String) The name (ID) of the region.
	 * @return (Region) The created region.
	 */
	public Region createRegion(String registerName) {
		if (position1 != null && position2 != null) {
			Region region = new Region(position1, position2, registerName);
			this.registeredRegions.add(region);
			return region;
		}
		
		return null;
	}
	
	/**
	 * Creates a region from the positions stored in memory.
	 * @return (Region) The instance created, or null if one or both of
	 * the positions aren't set.
	 */
	public Region createRegion() {
		if (position1 != null && position2 != null) {
            return new Region(position1, position2);
		}
		
		return null;
	}
	
	/**
	 * Checks wether the given locations is within the region expanded with
	 * a certain margin.
	 * @param region (Region)
	 * @param loc (Location)
	 * @param margin (int) The amount of blocks outside the area to check for.
	 * @return (boolean) True if within region with margin, false if not.
	 */
	public static boolean isWithinXZMargin(Region region, Location loc, int margin) {
		double p1X = region.getLocation(1).getX();
		double p1Z = region.getLocation(1).getZ();
		double p2X = region.getLocation(2).getX();
		double p2Z = region.getLocation(2).getZ();
		
		if (p1X <= p2X) {
			p1X -= margin;
			p2X += margin;
		} else {
			p1X += margin;
			p2X -= margin;
		}
		
		if (p1Z <= p2Z) {
			p1Z -= margin;
			p2Z += margin;
		} else {
			p1Z += margin;
			p2Z -= margin;
		}
		
		if (!loc.getWorld().equals(loc.getWorld())) {
			return false;
		}
		
		if ((p2X <= loc.getX() && loc.getX() < p1X) || (p1X <= loc.getX() && loc.getX() < p2X)) {
			if ((p2Z <= loc.getZ() && loc.getZ() < p1Z) || (p1Z <= loc.getZ() && loc.getZ() < p2Z)) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Checks wether the given locations is within the region expanded with
	 * a certain margin.
	 * @param region (Region)
	 * @param loc (Location)
	 * @param margin (int) The amount of blocks outside the area to check for.
	 * @return (boolean) True if within region with margin, false if not.
	 */
	public static boolean isWithinRegionMargin(Region region, Location loc, int margin) {
		double p1X = region.getLocation(1).getX();
		double p1Y = region.getLocation(1).getY();
		double p1Z = region.getLocation(1).getZ();
		double p2X = region.getLocation(2).getX();
		double p2Y = region.getLocation(2).getY();
		double p2Z = region.getLocation(2).getZ();
		
		if (p1X <= p2X) {
			p1X -= margin;
			p2X += margin;
		} else {
			p1X += margin;
			p2X -= margin;
		}
		
		if (p1Y <= p2Y) {
			p1Y -= margin;
			p2Y += margin;
		} else {
			p1Y += margin;
			p2Y -= margin;
		}
		
		if (p1Z <= p2Z) {
			p1Z -= margin;
			p2Z += margin;
		} else {
			p1Z += margin;
			p2Z -= margin;
		}
		
		if (!loc.getWorld().equals(loc.getWorld())) {
			return false;
		}
		
		if ((p2X <= loc.getX() && loc.getX() < p1X) || (p1X <= loc.getX() && loc.getX() < p2X)) {
			if ((p2Y <= loc.getY() && loc.getY() < p1Y) || (p1Y <= loc.getY() && loc.getY() < p2Y)) {
				if ((p2Z <= loc.getZ() && loc.getZ() < p1Z) || (p1Z <= loc.getZ() && loc.getZ() < p2Z)) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Checks wether the given locations is within the specified region.
	 * @param region (Region)
	 * @param loc (Location)
	 * @return (boolean) True if within, false if not.
	 */
	public static boolean isWithinRegion(Region region, Location loc) {
		double p1X = region.getLocation(1).getX();
		double p1Y = region.getLocation(1).getY();
		double p1Z = region.getLocation(1).getZ();
		double p2X = region.getLocation(2).getX();
		double p2Y = region.getLocation(2).getY();
		double p2Z = region.getLocation(2).getZ();
		
		if (!region.getLocation(1).getWorld().equals(loc.getWorld())) {
			return false;
		}
		
		if ((p2X <= loc.getX() && loc.getX() < p1X) || (p1X <= loc.getX() && loc.getX() < p2X)) {
			if ((p2Y <= loc.getY() && loc.getY() < p1Y) || (p1Y <= loc.getY() && loc.getY() < p2Y)) {
				if ((p2Z <= loc.getZ() && loc.getZ() < p1Z) || (p1Z <= loc.getZ() && loc.getZ() < p2Z)) {
					return true;
				}
			}
		}
		
		return false;
	}

	public List<Region> getRegisteredRegions() {
		return registeredRegions;
	}
	
}
