package com.kabryxis.thevoid.api.schematic;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import com.kabryxis.kabutils.data.Data;
import com.kabryxis.kabutils.spigot.data.Config;

public class Schematic {
	
	public final static Charset CHARSET = StandardCharsets.UTF_8;
	public final static String PATH = "plugins" + File.separator + "TheVoid" + File.separator + "schematics" + File.separator;
	public final static String SEPERATOR = ",", LINE_SEPERATOR = ";";
	
	@SuppressWarnings("deprecation")
	public static void create(String name, List<Block> blocks, Vector center, int radius) {
		StringBuilder builder = new StringBuilder();
		int size = blocks.size();
		int[][] data = new int[size][5];
		for(int i = 0; i < size; i++) {
			int[] d = data[i];
			Block block = blocks.get(i);
			int x = block.getX() - center.getBlockX(), y = block.getY() - center.getBlockY(), z = block.getZ() - center.getBlockZ(), id = block.getTypeId(), bd = block.getData();
			d[0] = x;
			builder.append(x);
			builder.append(SEPERATOR);
			d[1] = y;
			builder.append(y);
			builder.append(SEPERATOR);
			d[2] = z;
			builder.append(z);
			if(id == 0) {
				if(i < size - 1) builder.append(LINE_SEPERATOR);
				continue;
			}
			builder.append(SEPERATOR);
			d[3] = id;
			builder.append(id);
			if(bd == 0) {
				if(i < size - 1) builder.append(LINE_SEPERATOR);
				continue;
			}
			builder.append(SEPERATOR);
			d[4] = bd;
			builder.append(bd);
			if(i < size - 1) builder.append(LINE_SEPERATOR);
		}
		Data.write(Paths.get(PATH + name + ".sch"), builder.toString().getBytes(CHARSET));
		Config config = Config.get(new File(PATH + name + "-data.yml"));
		config.set("radius", radius);
		config.save();
	}
	
	public static void create(File file, Consumer<Schematic> future) {
		Data.read(file.toPath(), new Consumer<byte[]>() {
			
			@Override
			public void accept(byte[] bytes) {
				String fileData = new String(bytes, CHARSET);
				String[] lines = fileData.split(LINE_SEPERATOR);
				int size = lines.length;
				int[][] data = new int[size][5];
				for(int i = 0; i < size; i++) {
					String line = lines[i];
					String[] split = line.split(SEPERATOR);
					int[] d = data[i];
					d[0] = Integer.parseInt(split[0]);
					d[1] = Integer.parseInt(split[1]);
					d[2] = Integer.parseInt(split[2]);
					if(split.length > 3) d[3] = Integer.parseInt(split[3]);
					if(split.length > 4) d[4] = Integer.parseInt(split[4]);
				}
				Schematic schematic = new Schematic(file);
				schematic.reload();
				future.accept(schematic);
			}
			
		});
	}
	
	private final File file;
	private final String name;
	private final Config data;
	
	private int[][] schematicData;
	
	public Schematic(File file) {
		this.file = file;
		this.name = file.getName().split("\\.")[0];
		this.data = Config.get(new File(PATH + name + "-data.yml"));
		data.load();
	}
	
	public void reload() {
		Data.read(file.toPath(), bytes -> {
			String fileData = new String(bytes, CHARSET);
			String[] lines = fileData.split(LINE_SEPERATOR);
			int size = lines.length;
			schematicData = new int[size][5];
			for(int i = 0; i < size; i++) {
				String line = lines[i];
				String[] split = line.split(SEPERATOR);
				int[] d = schematicData[i];
				d[0] = Integer.parseInt(split[0]);
				d[1] = Integer.parseInt(split[1]);
				d[2] = Integer.parseInt(split[2]);
				if(split.length > 3) d[3] = Integer.parseInt(split[3]);
				if(split.length > 4) d[4] = Integer.parseInt(split[4]);
			}
		});
	}
	
	public String getName() {
		return name;
	}
	
	public int[][] getSchematicData() {
		return schematicData;
	}
	
	public int getRadius() {
		return data.getInt("radius");
	}
	
	public Config getData() {
		return data;
	}
	
	@Override
	public boolean equals(Object obj) {
		return obj instanceof Schematic ? name.equals(((Schematic)obj).getName()) : false;
	}
	
	@Override
	public String toString() {
		return "Schematic[" + name + "]";
	}
	
}
