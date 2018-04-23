package com.kabryxis.thevoid.api.arena.schematic;

import com.kabryxis.thevoid.api.arena.object.ArenaDataObjectable;

public interface BaseArenaData extends ArenaData, ArenaDataObjectable {
	
	int getRadius();
	
	//boolean isOdd();
	
	void setMinsAndMaxs(int lx, int ly, int lz, int mx, int mz);
	
	int getLowestY();
	
}
