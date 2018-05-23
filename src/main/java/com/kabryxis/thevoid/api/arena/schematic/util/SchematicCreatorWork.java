package com.kabryxis.thevoid.api.arena.schematic.util;

import org.bukkit.block.Block;

public interface SchematicCreatorWork {
	
	void next(SchematicCreator creator, Block block, SchematicEntry entry, int baseX, int baseY, int baseZ);
	
}
