package com.kabryxis.thevoid.api.impl.arena.schematic;

import com.kabryxis.kabutils.spigot.data.Config;
import com.kabryxis.thevoid.api.arena.schematic.BaseSchematic;
import com.kabryxis.thevoid.api.util.arena.schematic.SchematicEntry;

import java.io.File;
import java.util.Set;

public class VoidBaseSchematic extends VoidSchematic implements BaseSchematic {
	
	private final Config data;
	
	public VoidBaseSchematic(File file) {
		super(file);
		this.data = new Config(new File(VoidSchematic.PATH + getName() + "-data.yml"));
		data.load();
	}
	
	public VoidBaseSchematic(File file, String name, Set<SchematicEntry> schematicEntries, Config data, int sizeX, int sizeY, int sizeZ) {
		super(file, name, schematicEntries, sizeX, sizeY, sizeZ);
		this.data = data;
	}
	
	@Override
	public Config getData() {
		return data;
	}
	
	@Override
	public boolean isOdd() {
		return data.getBoolean("odd");
	}
	
	@Override
	public double getTimeModifier() {
		return data.getDouble("time-modifier");
	}
	
	@Override
	public int getWeight() {
		return data.getInt("weight");
	}
	
	@Override
	public double getRadius() {
		return data.getDouble("radius");
	}
	
	@Override
	public double getCenterX() {
		return data.getDouble("center.x");
	}
	
	@Override
	public double getCenterY() {
		return data.getDouble("center.y");
	}
	
	@Override
	public double getCenterZ() {
		return data.getDouble("center.z");
	}
	
}
