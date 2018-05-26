package com.kabryxis.thevoid.api.arena.schematic.impl;

import com.kabryxis.kabutils.spigot.data.Config;
import com.kabryxis.thevoid.api.arena.schematic.BaseSchematic;
import com.kabryxis.thevoid.api.arena.schematic.util.SchematicEntry;
import com.kabryxis.thevoid.api.arena.schematic.util.SchematicWork;

import java.util.Collections;
import java.util.Set;
import java.util.function.Supplier;

public class VoidEmptySchematic implements BaseSchematic {
	
	private final Config emptyData = new Config();
	
	@Override
	public String getName() {
		return "empty";
	}
	
	@Override
	public Set<SchematicEntry> getSchematicEntries() {
		return Collections.emptySet();
	}
	
	@Override
	public double getSizeX() {
		return 0;
	}
	
	@Override
	public double getSizeZ() {
		return 0;
	}
	
	@Override
	public void addSchematicWork(Supplier<? extends SchematicWork> clazz) {}
	
	@Override
	public boolean hasSchematicWork() {
		return false;
	}
	
	@Override
	public Set<Supplier<? extends SchematicWork>> getSchematicWork() {
		return Collections.emptySet();
	}
	
	@Override
	public Config getData() {
		return emptyData;
	}
	
	@Override
	public double getTimeModifier() {
		return 1.0;
	}
	
	@Override
	public boolean isOdd() {
		return true;
	}
	
	@Override
	public double getRadius() {
		return 0;
	}
	
	@Override
	public double getCenterX() {
		return 0;
	}
	
	@Override
	public double getCenterY() {
		return 0;
	}
	
	@Override
	public double getCenterZ() {
		return 0;
	}
	
	@Override
	public int getWeight() {
		return 100;
	}
	
}
