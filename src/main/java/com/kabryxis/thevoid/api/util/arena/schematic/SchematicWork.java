package com.kabryxis.thevoid.api.util.arena.schematic;

import org.bukkit.Material;
import org.bukkit.block.Block;

public interface SchematicWork {
	
	void doExtra(Block block, Material futureType, int futureData);
	
}
