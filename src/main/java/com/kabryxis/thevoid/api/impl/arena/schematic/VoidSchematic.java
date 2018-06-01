package com.kabryxis.thevoid.api.impl.arena.schematic;

import com.kabryxis.kabutils.data.Data;
import com.kabryxis.thevoid.api.arena.schematic.Schematic;
import com.kabryxis.thevoid.api.util.arena.schematic.SchematicEntry;
import com.kabryxis.thevoid.api.util.arena.schematic.SchematicWork;
import org.bukkit.Material;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class VoidSchematic implements Schematic {
	
	public final static Charset CHARSET = StandardCharsets.UTF_8;
	public final static String PATH = "plugins" + File.separator + "TheVoid" + File.separator + "schematics" + File.separator;
	public final static String SEPERATOR = ",", LINE_SEPERATOR = ";";
	
	private final String name;
	
	private File file;
	private Set<SchematicEntry> schematicEntries;
	private Set<Supplier<? extends SchematicWork>> schematicWorks;
	private int sizeX, sizeY, sizeZ;
	
	public VoidSchematic(File file) {
		this.file = file;
		this.name = file.getName().split("\\.")[0];
		reload();
	}
	
	public VoidSchematic(File file, String name, Set<SchematicEntry> schematicEntries, int sizeX, int sizeY, int sizeZ) {
		this.file = file;
		this.name = name;
		this.schematicEntries = schematicEntries;
		this.sizeX = sizeX;
		this.sizeY = sizeY;
		this.sizeZ = sizeZ;
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
			schematicEntries = new HashSet<>(size);
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
	public Set<SchematicEntry> getSchematicEntries() {
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
