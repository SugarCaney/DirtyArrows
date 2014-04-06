package nl.SugCube.DirtyArrows;

import org.bukkit.Location;

public class Region {
	
	private Location loc1;
	private Location loc2;
	private String name;
	
	public Region(Location pos1, Location pos2) {
		this.loc1 = pos1;
		this.loc2 = pos2;
	}
	
	public Region(Location pos1, Location pos2, String name) {
		this.loc1 = pos1;
		this.loc2 = pos2;
		this.setName(name);
	}
	
	public void setLocation(Location pos, int id) {
		if (id == 1) {
			this.loc1 = pos;
		} else {
			this.loc2 = pos;
		}
	}
	
	public Location getLocation(int id) {
		if (id == 1) {
			return this.loc1;
		} else {
			return this.loc2;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
