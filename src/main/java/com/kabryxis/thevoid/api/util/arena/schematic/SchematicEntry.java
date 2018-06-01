package com.kabryxis.thevoid.api.util.arena.schematic;

import com.kabryxis.thevoid.api.util.arena.ArenaEntry;
import org.bukkit.Material;

public class SchematicEntry {
	
	private int x, y, z;
	private Material type;
	private int data; // TODO 1.13 update
	
	public SchematicEntry(int x, int y, int z, Material type, int data) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.type = type;
		this.data = data;
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
	
	public ArenaEntry toArenaEntry(int offsetX, int offsetY, int offsetZ) {
		return new ArenaEntry(x + offsetX, y + offsetY, z + offsetZ, type.getId(), data);
	}
	
}
