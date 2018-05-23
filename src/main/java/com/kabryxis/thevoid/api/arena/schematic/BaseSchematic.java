package com.kabryxis.thevoid.api.arena.schematic;

import com.kabryxis.kabutils.spigot.data.Config;

public interface BaseSchematic extends Schematic {
	
	Config getData();
	
	boolean isOdd();
	
	double getRadius();
	
	double getTimeModifier();
	
	double getCenterX();
	
	double getCenterY();
	
	double getCenterZ();
	
}
