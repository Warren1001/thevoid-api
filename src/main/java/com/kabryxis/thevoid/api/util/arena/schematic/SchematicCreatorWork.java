package com.kabryxis.thevoid.api.util.arena.schematic;

import org.bukkit.block.Block;

public interface SchematicCreatorWork {
	
	void next(SchematicCreator creator, Block block, SchematicEntry entry, int baseX, int baseY, int baseZ);
	
}
