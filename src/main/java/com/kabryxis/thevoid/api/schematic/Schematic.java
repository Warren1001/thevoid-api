package com.kabryxis.thevoid.api.schematic;

import com.kabryxis.kabutils.data.Data;
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

public class Schematic {
	
	public final static Charset CHARSET = StandardCharsets.UTF_8;
	public final static String PATH = "plugins" + File.separator + "TheVoid" + File.separator + "schematics" + File.separator;
	public final static String SEPERATOR = ",", LINE_SEPERATOR = ";";
	
	private final File file;
	private final String name;
	
	private List<SchematicEntry> schematicData;
	private Set<Class<? extends SchematicWork>> schematicWorks;
	private double sizeX, sizeY, sizeZ;
	
	public Schematic(File file) {
		this.file = file;
		this.name = file.getName().split("\\.")[0];
		reload();
	}
	
	public Schematic(File file, String name, List<SchematicEntry> schematicData) {
		this.file = file;
		this.name = name;
		this.schematicData = schematicData;
	}
	
	@SuppressWarnings("deprecation")
	public Schematic(String name, BlockSelection selection, boolean air) {
		StringBuilder builder = new StringBuilder();
		List<Block> blocks = selection.getBlocks();
		int size = blocks.size(), lowestX = selection.getLowestX(), lowestY = selection.getLowestY(), lowestZ = selection.getLowestZ();
		List<SchematicEntry> schematicData = new ArrayList<>(size);
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
				schematicData.add(new SchematicEntry(x, y, z));
				continue;
			}
			builder.append(SEPERATOR);
			builder.append(type.toString().toLowerCase());
			if(data == 0) {
				if(i < size - 1) builder.append(LINE_SEPERATOR);
				schematicData.add(new SchematicEntry(x, y, z, type));
				continue;
			}
			builder.append(SEPERATOR);
			builder.append(data);
			if(i < size - 1) builder.append(LINE_SEPERATOR);
			schematicData.add(new SchematicEntry(x, y, z, type, data));
		}
		String filePath = PATH + name + ".sch";
		Data.write(Paths.get(filePath), builder.toString().getBytes(CHARSET));
		this.file = new File(filePath);
		this.name = name;
		this.schematicData = schematicData;
	}
	
	
	
	public void reload() {
		reload(null);
	}
	
	private void reload(Consumer<Schematic> future) {
		sizeX = 0;
		sizeY = 0;
		sizeZ = 0;
		Data.read(file.toPath(), bytes -> {
			String fileData = new String(bytes, CHARSET);
			String[] lines = fileData.split(LINE_SEPERATOR);
			int size = lines.length;
			schematicData = new ArrayList<>(size);
			for(String line : lines) {
				String[] split = line.split(SEPERATOR);
				int x = Integer.parseInt(split[0]);
				int y = Integer.parseInt(split[1]);
				int z = Integer.parseInt(split[2]);
				Material type = split.length > 3 ? Material.getMaterial(split[3].toUpperCase()) : Material.AIR;
				int data = split.length > 4 ? Integer.parseInt(split[4]) : 0;
				schematicData.add(new SchematicEntry(x, y, z, type, data));
				if(x > sizeX) sizeX = x + 1;
				if(y > sizeY) sizeY = y + 1;
				if(z > sizeZ) sizeZ = z + 1;
			}
			if(future != null) future.accept(this);
		});
	}
	
	public void addSchematicWork(Class<? extends SchematicWork> clazz) {
		if(schematicWorks == null) schematicWorks = new HashSet<>();
		schematicWorks.add(clazz);
	}
	
	public boolean hasSchematicWork() {
		return schematicWorks != null && !schematicWorks.isEmpty();
	}
	
	public Set<Class<? extends SchematicWork>> getSchematicWork() {
		return schematicWorks;
	}
	
	public String getName() {
		return name;
	}
	
	public List<SchematicEntry> getSchematicData() {
		return schematicData;
	}
	
	public double getSizeX() {
		return sizeX;
	}
	
	public double getSizeY() {
		return sizeY;
	}
	
	public double getSizeZ() {
		return sizeZ;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Schematic && name.equals(((Schematic)obj).getName());
	}
	
	@Override
	public String toString() {
		return "Schematic[name=" + name + "]";
	}
	
}
