package com.kabryxis.thevoid.api.util.arena.schematic;

import com.kabryxis.kabutils.spigot.data.Config;
import com.kabryxis.thevoid.api.arena.schematic.BaseSchematic;
import com.kabryxis.thevoid.api.arena.schematic.Schematic;

public interface SchematicCreator {
	
	void reset();
	
	SchematicCreator name(String name);
	
	SchematicCreator useData(boolean useData);
	
	boolean useData();
	
	Config getData();
	
	SchematicCreator includeAir(boolean includeAir);
	
	SchematicCreator center(double centerX, double centerY, double centerZ);
	
	SchematicCreator odd(boolean odd);
	
	SchematicCreator radius(double radius);
	
	SchematicCreator weight(int weight);
	
	SchematicCreator timeModifier(double timeModifier);
	
	SchematicCreator useExtraWork(String dataKey);
	
	BaseSchematic createBase();
	
	default Schematic createNormal() {
		return createNormal(false);
	}
	
	Schematic createNormal(boolean force);
	
}
