package com.kabryxis.thevoid.api.arena.object;

import com.kabryxis.kabutils.utility.BiSupplier;
import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.arena.schematic.BaseSchematic;

public interface ArenaDataObjectRegistry {
	
	<T extends ArenaDataObject> void register(String key, Class<T> clazz, BiSupplier<T, ? super BaseSchematic, ? super Arena> supplier);
	
	void handle(ArenaDataObjectable dataObjectable);
	
}
