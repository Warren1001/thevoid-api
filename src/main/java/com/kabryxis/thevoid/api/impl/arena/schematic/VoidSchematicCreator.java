package com.kabryxis.thevoid.api.impl.arena.schematic;

import com.kabryxis.kabutils.data.Data;
import com.kabryxis.kabutils.spigot.data.Config;
import com.kabryxis.thevoid.api.impl.game.VoidPlayer;
import com.kabryxis.thevoid.api.util.arena.schematic.BlockSelection;
import com.kabryxis.thevoid.api.util.arena.schematic.SchematicCreator;
import com.kabryxis.thevoid.api.util.arena.schematic.SchematicCreatorWork;
import com.kabryxis.thevoid.api.util.arena.schematic.SchematicEntry;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;

public class VoidSchematicCreator implements SchematicCreator {
	
	private static Map<String, SchematicCreatorWork> extraWork;
	
	public static void registerExtraWork(String dataKey, SchematicCreatorWork work) {
		if(extraWork == null) extraWork = new HashMap<>();
		extraWork.put(dataKey, work);
	}
	
	private VoidPlayer owner;
	
	public VoidSchematicCreator(VoidPlayer owner) {
		this.owner = owner;
	}
	
	@Override
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
	
	@Override
	public VoidSchematicCreator name(String name) {
		owner.sendMessage("using name '" + name + "' for schematic creation");
		this.name = name;
		this.file = new File(VoidSchematic.PATH + name + ".sch");
		return this;
	}
	
	private boolean useData;
	private Config data;
	
	@Override
	public VoidSchematicCreator useData(boolean useData) {
		if(!this.useData && useData) {
			owner.sendMessage("using data");
			data = new Config(new File(VoidSchematic.PATH + name + "-data.yml"));
			odd(true);
			radius(7.5);
			timeModifier(1.0);
			weight(100);
		}
		this.useData = useData;
		return this;
	}
	
	@Override
	public boolean useData() {
		return useData;
	}
	
	@Override
	public Config getData() {
		return data;
	}
	
	private boolean includeAir = false;
	
	@Override
	public VoidSchematicCreator includeAir(boolean includeAir) {
		owner.sendMessage("including air in schematic: " + includeAir);
		this.includeAir = includeAir;
		return this;
	}
	
	private double centerX, centerY, centerZ;
	private boolean centerSet = false;
	
	@Override
	public VoidSchematicCreator center(double centerX, double centerY, double centerZ) {
		owner.sendMessage("setting center of schematic to " + centerX + ", " + centerY + ", " + centerZ);
		this.centerX = centerX;
		this.centerY = centerY;
		this.centerZ = centerZ;
		centerSet = true;
		return this;
	}
	
	@Override
	public VoidSchematicCreator odd(boolean odd) {
		owner.sendMessage("schematic is odd: " + odd);
		data.set("odd", odd);
		return this;
	}
	
	@Override
	public VoidSchematicCreator radius(double radius) {
		owner.sendMessage("setting radius to " + radius);
		data.set("radius", radius);
		return this;
	}
	
	@Override
	public VoidSchematicCreator weight(int weight) {
		owner.sendMessage("setting weight to " + weight);
		data.set("weight", weight);
		return this;
	}
	
	@Override
	public VoidSchematicCreator timeModifier(double timeModifier) {
		owner.sendMessage("setting time-modifier to " + timeModifier);
		data.set("time-modifier", timeModifier);
		return this;
	}
	
	private Set<SchematicCreatorWork> extraWorks;
	
	@Override
	public VoidSchematicCreator useExtraWork(String dataKey) {
		if(extraWork == null) return this;
		SchematicCreatorWork work = extraWork.get(dataKey);
		if(work == null) return this;
		if(extraWorks == null) extraWorks = new HashSet<>();
		owner.sendMessage("using extrawork named '" + dataKey + "'");
		extraWorks.add(work);
		return this;
	}
	
	@Override
	public VoidBaseSchematic createBase() {
		if(!useData) {
			owner.sendMessage("no data has been specified, please do so to create base schematic");
			return null;
		}
		if(!centerSet) {
			owner.sendMessage("you did not specify a center to this schematic, please set one.");
			return null;
		}
		owner.sendMessage("creating base schematic named '" + name + "'");
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
	
	@Override
	public VoidSchematic createNormal(boolean force) {
		if(!forceCheck && !force && useData) {
			owner.sendMessage("creating a normal schematic when data is being used, you sure? type again if so..");
			forceCheck = true;
			return null;
		}
		forceCheck = false;
		owner.sendMessage("creating normal schematic with name '" + name + "'");
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
