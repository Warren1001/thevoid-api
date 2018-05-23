package com.kabryxis.thevoid.api.arena.schematic;

import com.kabryxis.thevoid.api.arena.object.ArenaDataObjectable;
import org.bukkit.Location;

public interface BaseArenaData extends ArenaData, ArenaDataObjectable {
	
	Location getCenter();
	
	double getRadius();
	
	//boolean isOdd();
	
	void setMinsAndMaxs(int lx, int ly, int lz, int mx, int my, int mz);
	
	int getLowestY();
	
}
