package com.kabryxis.thevoid.api.arena.schematic.impl;

import com.kabryxis.kabutils.data.Data;
import com.kabryxis.thevoid.api.arena.schematic.ISchematic;
import com.kabryxis.thevoid.api.arena.schematic.util.BlockSelection;
import com.kabryxis.thevoid.api.arena.schematic.util.SchematicEntry;
import com.kabryxis.thevoid.api.arena.schematic.util.SchematicWork;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class VoidSchematic implements ISchematic {
	
	public final static Charset CHARSET = StandardCharsets.UTF_8;
	public final static String PATH = "plugins" + File.separator + "TheVoid" + File.separator + "schematics" + File.separator;
	public final static String SEPERATOR = ",", LINE_SEPERATOR = ";";
	
	private final File file;
	private final String name;
	
	private List<SchematicEntry> schematicEntries;
	private Set<Supplier<? extends SchematicWork>> schematicWorks;
	private double sizeX, sizeY, sizeZ;
	
	public VoidSchematic(File file) {
		this.file = file;
		this.name = file.getName().split("\\.")[0];
		reload();
	}
	
	public VoidSchematic(File file, String name, List<SchematicEntry> schematicEntries) {
		this.file = file;
		this.name = name;
		this.schematicEntries = schematicEntries;
		// TODO sizes
	}
	
	public VoidSchematic(String name, BlockSelection selection, boolean air, int centerX, int centerY, int centerZ) {
		StringBuilder builder = new StringBuilder();
		List<Block> blocks = selection.getBlocks();
		int size = blocks.size(), lowestX = selection.getLowestX(), lowestY = selection.getLowestY(), lowestZ = selection.getLowestZ();
		List<SchematicEntry> schematicEntries = new ArrayList<>(size);
		for(int i = 0; i < size; i++) {
			Block block = blocks.get(i);
			Material type = block.getType();
			if(!air && type == Material.AIR) continue;
			int x = block.getX() - lowestX, y = block.getY() - lowestY, z = block.getZ() - lowestZ, data = block.getData();
			builder.append(x);
			builder.append(SEPERATOR);
			builder.append(y);
			builder.append(SEPERATOR);
			builder.append(z);
			if(type == Material.AIR) {
				if(i < size - 1) builder.append(LINE_SEPERATOR);
				schematicEntries.add(new SchematicEntry(x - centerX, y - centerY, z - centerZ));
				if(x > sizeX) sizeX = x + 1;
				if(y > sizeY) sizeY = y + 1;
				if(z > sizeZ) sizeZ = z + 1;
				continue;
			}
			builder.append(SEPERATOR);
			builder.append(type.toString().toLowerCase());
			if(data == 0) {
				if(i < size - 1) builder.append(LINE_SEPERATOR);
				schematicEntries.add(new SchematicEntry(x - centerX, y - centerY, z - centerZ, type));
				if(x > sizeX) sizeX = x + 1;
				if(y > sizeY) sizeY = y + 1;
				if(z > sizeZ) sizeZ = z + 1;
				continue;
			}
			builder.append(SEPERATOR);
			builder.append(data);
			if(i < size - 1) builder.append(LINE_SEPERATOR);
			schematicEntries.add(new SchematicEntry(x - centerX, y - centerY, z - centerZ, type, data));
			if(x > sizeX) sizeX = x + 1;
			if(y > sizeY) sizeY = y + 1;
			if(z > sizeZ) sizeZ = z + 1;
		}
		String filePath = PATH + name + ".sch";
		Data.write(Paths.get(filePath), builder.toString().getBytes(CHARSET));
		this.file = new File(filePath);
		this.name = name;
		this.schematicEntries = schematicEntries;
	}
	
	public void reload() {
		reload(null);
	}
	
	private void reload(Consumer<VoidSchematic> future) {
		Data.read(file.toPath(), bytes -> {
			sizeX = 0;
			sizeY = 0;
			sizeZ = 0;
			String fileData = new String(bytes, CHARSET);
			String[] lines = fileData.split(LINE_SEPERATOR);
			int size = lines.length;
			schematicEntries = new ArrayList<>(size);
			for(String line : lines) {
				String[] split = line.split(SEPERATOR);
				int x = Integer.parseInt(split[0]);
				int y = Integer.parseInt(split[1]);
				int z = Integer.parseInt(split[2]);
				Material type = split.length > 3 ? Material.getMaterial(split[3].toUpperCase()) : Material.AIR;
				int data = split.length > 4 ? Integer.parseInt(split[4]) : 0;
				schematicEntries.add(new SchematicEntry(x, y, z, type, data));
				if(x > sizeX) sizeX = x + 1;
				if(y > sizeY) sizeY = y + 1;
				if(z > sizeZ) sizeZ = z + 1;
			}
			if(future != null) future.accept(this);
		});
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public List<SchematicEntry> getSchematicEntries() {
		return schematicEntries;
	}
	
	@Override
	public double getSizeX() {
		return sizeX;
	}
	
	@Override
	public double getSizeZ() {
		return sizeZ;
	}
	
	@Override
	public void addSchematicWork(Supplier<? extends SchematicWork> supplier) {
		if(schematicWorks == null) schematicWorks = new HashSet<>();
		schematicWorks.add(supplier);
	}
	
	@Override
	public boolean hasSchematicWork() {
		return schematicWorks != null && !schematicWorks.isEmpty();
	}
	
	@Override
	public Set<Supplier<? extends SchematicWork>> getSchematicWork() {
		return schematicWorks;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof VoidSchematic && name.equals(((VoidSchematic)obj).getName());
	}
	
	@Override
	public String toString() {
		return "VoidSchematic[name=" + name + "]";
	}
	
}
