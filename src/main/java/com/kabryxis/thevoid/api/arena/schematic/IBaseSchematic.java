package com.kabryxis.thevoid.api.arena.schematic;

import com.kabryxis.kabutils.spigot.data.Config;

public interface IBaseSchematic extends ISchematic {
	
	Config getData();
	
	boolean isOdd();
	
	int getRadius();
	
	double getTimeModifier();
	
}
