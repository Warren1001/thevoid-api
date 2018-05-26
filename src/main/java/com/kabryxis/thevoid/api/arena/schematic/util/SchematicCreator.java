package com.kabryxis.thevoid.api.arena.schematic.util;

import com.kabryxis.kabutils.data.Data;
import com.kabryxis.kabutils.spigot.data.Config;
import com.kabryxis.thevoid.api.arena.schematic.impl.VoidBaseSchematic;
import com.kabryxis.thevoid.api.arena.schematic.impl.VoidSchematic;
import com.kabryxis.thevoid.api.game.Gamer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

public class SchematicCreator {
	
	private static Map<String, SchematicCreatorWork> extraWork;
	
	public static void registerExtraWork(String dataKey, SchematicCreatorWork work) {
		if(extraWork == null) extraWork = new HashMap<>();
		extraWork.put(dataKey, work);
	}
	
	private Gamer owner;
	
	public SchematicCreator(Gamer owner) {
		this.owner = owner;
	}
	
	public void reset() {
		name = null;
		file = null;
		useData = false;
		data = null;
		includeAir = false;
		centerX = 0;
		centerY = 0;
		centerZ = 0;
		centerSet = false;
		extraWorks = null;
		forceCheck = false;
	}
	
	private String name;
	private File file;
	
	public SchematicCreator name(String name) {
		owner.message("using name '" + name + "' for schematic creation");
		this.name = name;
		this.file = new File(VoidSchematic.PATH + name + ".sch");
		return this;
	}
	
	private boolean useData;
	private Config data;
	
	public SchematicCreator useData(boolean useData) {
		if(!this.useData && useData) {
			owner.message("using data");
			data = new Config(new File(VoidSchematic.PATH + name + "-data.yml"));
			odd(true);
			radius(7.5);
			timeModifier(1.0);
			weight(100);
		}
		this.useData = useData;
		return this;
	}
	
	public boolean useData() {
		return useData;
	}
	
	public Config getData() {
		return data;
	}
	
	private boolean includeAir = false;
	
	public SchematicCreator includeAir(boolean includeAir) {
		owner.message("including air in schematic: " + includeAir);
		this.includeAir = includeAir;
		return this;
	}
	
	private double centerX, centerY, centerZ;
	private boolean centerSet = false;
	
	public SchematicCreator center(double centerX, double centerY, double centerZ) {
		owner.message("setting center of schematic to " + centerX + ", " + centerY + ", " + centerZ);
		this.centerX = centerX;
		this.centerY = centerY;
		this.centerZ = centerZ;
		centerSet = true;
		return this;
	}
	
	public SchematicCreator odd(boolean odd) {
		owner.message("schematic is odd: " + odd);
		data.set("odd", odd);
		return this;
	}
	
	public SchematicCreator radius(double radius) {
		owner.message("setting radius to " + radius);
		data.set("radius", radius);
		return this;
	}
	
	public SchematicCreator weight(int weight) {
		owner.message("setting weight to " + weight);
		data.set("weight", weight);
		return this;
	}
	
	public SchematicCreator timeModifier(double timeModifier) {
		owner.message("setting time-modifier to " + timeModifier);
		data.set("time-modifier", timeModifier);
		return this;
	}
	
	private Set<SchematicCreatorWork> extraWorks;
	
	public SchematicCreator useExtraWork(String dataKey) {
		if(extraWork == null) return this;
		SchematicCreatorWork work = extraWork.get(dataKey);
		if(work == null) return this;
		if(extraWorks == null) extraWorks = new HashSet<>();
		owner.message("using extrawork named '" + dataKey + "'");
		extraWorks.add(work);
		return this;
	}
	
	public VoidBaseSchematic createBase() {
		if(!useData) {
			owner.message("no data has been specified, please do so to create base schematic");
			return null;
		}
		if(!centerSet) {
			owner.message("you did not specify a center to this schematic, please set one.");
			return null;
		}
		owner.message("creating base schematic named '" + name + "'");
		StringBuilder builder = new StringBuilder();
		BlockSelection selection = owner.getSelection();
		Set<Block> blocks = selection.getBlocks();
		int lowestX = selection.getLowestX(), lowestY = selection.getLowestY(), lowestZ = selection.getLowestZ();
		double cx = centerX - lowestX, cy = centerY - lowestY, cz = centerZ - lowestZ;
		Set<SchematicEntry> schematicEntries = new HashSet<>(blocks.size());
		int sizeX = Integer.MIN_VALUE, sizeY = Integer.MIN_VALUE, sizeZ = Integer.MIN_VALUE;
		for(Iterator<Block> iterator = blocks.iterator(); iterator.hasNext();) {
			Block block = iterator.next();
			Material type = block.getType();
			if(type == Material.AIR && !includeAir) continue;
			int x = block.getX() - lowestX, y = block.getY() - lowestY, z = block.getZ() - lowestZ;
			int data = block.getData();
			SchematicEntry schematicEntry = new SchematicEntry(x, y, z, type, data);
			schematicEntries.add(schematicEntry);
			if(extraWorks != null) extraWorks.forEach(work -> work.next(this, block, schematicEntry, lowestX, lowestY, lowestZ));
			if(x > sizeX) sizeX = x + 1;
			if(y > sizeY) sizeY = y + 1;
			if(z > sizeZ) sizeZ = z + 1;
			builder.append(x);
			builder.append(VoidSchematic.SEPERATOR);
			builder.append(y);
			builder.append(VoidSchematic.SEPERATOR);
			builder.append(z);
			if(type != Material.AIR) {
				builder.append(VoidSchematic.SEPERATOR);
				builder.append(type.toString().toLowerCase());
				if(data != 0) {
					builder.append(VoidSchematic.SEPERATOR);
					builder.append(data);
				}
			}
			if(iterator.hasNext()) builder.append(VoidSchematic.LINE_SEPERATOR);
		}
		ConfigurationSection centerSection = data.createSection("center");
		centerSection.set("x", cx);
		centerSection.set("y", cy);
		centerSection.set("z", cz);
		Data.write(Paths.get(file.getPath()), builder.toString().getBytes(VoidSchematic.CHARSET));
		data.save();
		return new VoidBaseSchematic(file, name, schematicEntries, data, sizeX, sizeY, sizeZ);
	}
	
	private boolean forceCheck = false;
	
	public VoidSchematic createNormal() {
		return createNormal(false);
	}
	
	public VoidSchematic createNormal(boolean force) {
		if(!forceCheck && !force && useData) {
			owner.message("creating a normal schematic when data is being used, you sure? type again if so..");
			forceCheck = true;
			return null;
		}
		forceCheck = false;
		owner.message("creating normal schematic with name '" + name + "'");
		StringBuilder builder = new StringBuilder();
		BlockSelection selection = owner.getSelection();
		Set<Block> blocks = selection.getBlocks();
		int lowestX = selection.getLowestX(), lowestY = selection.getLowestY(), lowestZ = selection.getLowestZ();
		Set<SchematicEntry> schematicEntries = new HashSet<>(blocks.size());
		int sizeX = Integer.MIN_VALUE, sizeY = Integer.MIN_VALUE, sizeZ = Integer.MIN_VALUE;
		for(Iterator<Block> iterator = blocks.iterator(); iterator.hasNext();) {
			Block block = iterator.next();
			Material type = block.getType();
			if(type == Material.AIR && !includeAir) continue;
			int x = block.getX() - lowestX, y = block.getY() - lowestY, z = block.getZ() - lowestZ;
			int data = block.getData();
			SchematicEntry schematicEntry = new SchematicEntry(x, y, z, type, data);
			schematicEntries.add(schematicEntry);
			if(extraWorks != null) extraWorks.forEach(work -> work.next(this, block, schematicEntry, lowestX, lowestY, lowestZ));
			if(x > sizeX) sizeX = x + 1;
			if(y > sizeY) sizeY = y + 1;
			if(z > sizeZ) sizeZ = z + 1;
			builder.append(x);
			builder.append(VoidSchematic.SEPERATOR);
			builder.append(y);
			builder.append(VoidSchematic.SEPERATOR);
			builder.append(z);
			if(type != Material.AIR) {
				builder.append(VoidSchematic.SEPERATOR);
				builder.append(type.toString().toLowerCase());
				if(data != 0) {
					builder.append(VoidSchematic.SEPERATOR);
					builder.append(data);
				}
			}
			if(iterator.hasNext()) builder.append(VoidSchematic.LINE_SEPERATOR);
		}
		Data.write(Paths.get(file.getPath()), builder.toString().getBytes(VoidSchematic.CHARSET));
		return new VoidSchematic(file, name, schematicEntries, sizeX, sizeY, sizeZ);
	}
	
}
