package com.kabryxis.thevoid.api.arena.schematic;

import com.kabryxis.kabutils.random.weighted.Weighted;
import com.kabryxis.kabutils.spigot.data.Config;

public interface BaseSchematic extends Schematic, Weighted {
	
	Config getData();
	
	boolean isOdd();
	
	double getRadius();
	
	double getTimeModifier();
	
	double getCenterX();
	
	double getCenterY();
	
	double getCenterZ();
	
}
