package com.kabryxis.thevoid.api.arena.object;

import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.arena.schematic.IBaseSchematic;

public interface IArenaDataObjectRegistry {
	
	<T extends ArenaDataObject> void register(String key, Class<T> clazz, BiSupplier<T, ? super IBaseSchematic, ? super Arena> supplier);
	
	void handle(ArenaDataObjectable dataObjectable);
	
}
