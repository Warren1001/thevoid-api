package com.kabryxis.thevoid.api.arena.schematic.impl;

import com.kabryxis.kabutils.random.Weighted;
import com.kabryxis.kabutils.spigot.data.Config;
import com.kabryxis.thevoid.api.arena.schematic.IBaseSchematic;
import com.kabryxis.thevoid.api.arena.schematic.util.BlockSelection;

import java.io.File;

public class VoidBaseSchematic extends VoidSchematic implements IBaseSchematic, Weighted {
	
	private final Config data;
	
	public VoidBaseSchematic(File file) {
		super(file);
		this.data = Config.get(new File(VoidSchematic.PATH + getName() + "-data.yml"));
		data.load();
	}
	
	public VoidBaseSchematic(String name, BlockSelection selection, double centerX, double centerY, double centerZ, int radius, int weight, boolean odd) {
		super(name, selection, false, (int)centerX, (int)centerY, (int)centerZ);
		this.data = Config.get(new File(PATH + name + "-data.yml"));
		//double lowestX = selection.getLowestX(), lowestY = selection.getLowestY(), lowestZ = selection.getLowestZ();
		data.set("odd", odd);
		data.set("radius", radius);
		data.set("weight", weight);
		data.set("time-modifier", 1.0);
		/*ConfigurationSection cent = data.createSection("center");
		cent.set("x", centerX - lowestX);
		cent.set("y", centerY - lowestY);
		cent.set("z", centerZ - lowestZ);*/
		data.save();
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
	public int getRadius() {
		return data.getInt("radius");
	}
	
}
