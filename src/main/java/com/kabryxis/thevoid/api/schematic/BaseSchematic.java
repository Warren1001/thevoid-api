package com.kabryxis.thevoid.api.schematic;

import com.kabryxis.kabutils.random.Weighted;
import com.kabryxis.kabutils.spigot.data.Config;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;

public class BaseSchematic extends Schematic implements Weighted {
	
	private final Config data;
	
	public BaseSchematic(File file) {
		super(file);
		this.data = Config.get(new File(PATH + getName() + "-data.yml"));
		data.load();
	}
	
	public BaseSchematic(String name, BlockSelection selection, double centerX, double centerY, double centerZ, int radius, int weight, boolean odd) {
		super(name, selection, false);
		this.data = Config.get(new File(PATH + name + "-data.yml"));
		double lowestX = selection.getLowestX(), lowestY = selection.getLowestY(), lowestZ = selection.getLowestZ();
		data.set("odd", odd);
		data.set("radius", radius);
		data.set("weight", weight);
		data.set("time-modifier", 1.0);
		ConfigurationSection cent = data.createSection("center");
		cent.set("x", centerX - lowestX);
		cent.set("y", centerY - lowestY);
		cent.set("z", centerZ - lowestZ);
		data.save();
	}
	
	public boolean isOdd() {
		return data.getBoolean("odd");
	}
	
	public int getRadius() {
		return data.getInt("radius");
	}
	
	@Override
	public int getWeight() {
		return data.getInt("weight");
	}
	
	public double getTimeModifier() {
		return data.getDouble("time-modifier");
	}
	
	public Config getData() {
		return data;
	}
	
	public double getCenterX() {
		return data.getDouble("center.x");
	}
	
	public double getCenterY() {
		return data.getDouble("center.y");
	}
	
	public double getCenterZ() {
		return data.getDouble("center.z");
	}
	
}
