package com.kabryxis.thevoid.api.arena.schematic.util;

import com.kabryxis.kabutils.spigot.data.Config;
import org.bukkit.block.Block;

public class CreatorWalkable implements SchematicCreatorWork {
	
	@Override
	public void next(SchematicCreator creator, Block block, SchematicEntry entry, int baseX, int baseY, int baseZ) {
		if(creator.useData() && block.hasMetadata("walkable")) {
			Config data = creator.getData();
			String walkableData = data.getString("walkable");
			String y = String.valueOf(entry.getY());
			data.set("walkable", walkableData == null ? y : walkableData + "," + y);
		}
	}
	
}
