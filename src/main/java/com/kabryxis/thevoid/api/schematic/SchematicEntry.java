package com.kabryxis.thevoid.api.schematic;

import org.bukkit.Material;

public class SchematicEntry {
	
	private int x, y, z;
	private Material type;
	private int data; // TODO 1.13 update
	
	private String metadata;
	
	public SchematicEntry(int x, int y, int z, Material type, int data, String metadata) {
		this(x, y, z, type, data);
		this.metadata = metadata;
	}
	
	public SchematicEntry(int x, int y, int z, Material type, int data) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
		this.data = data;
	}
	
	public SchematicEntry(int x, int y, int z, Material type) {
		this(x, y, z, type, 0);
	}
	
	public SchematicEntry(int x, int y, int z) {
		this(x, y, z, Material.AIR);
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}
	
	public Material getType() {
		return type;
	}
	
	public int getData() {
		return data;
	}
	
	public String getMetadata() {
		return metadata;
	}
	
}
