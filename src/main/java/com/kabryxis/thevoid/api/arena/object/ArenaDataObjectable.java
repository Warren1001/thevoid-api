package com.kabryxis.thevoid.api.arena.object;

import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.arena.schematic.BaseSchematic;

import java.util.Set;
import java.util.function.Consumer;

public interface ArenaDataObjectable {
	
	BaseSchematic getSchematic();
	
	Arena getArena();
	
	void registerDataObject(ArenaDataObject dataObject);
	
	<T extends ArenaDataObject> T getDataObject(Class<T> clazz);
	
	boolean hasDataObject(Class<? extends ArenaDataObject> clazz);
	
	void forEachDataObject(Consumer<? super ArenaDataObject> action);
	
	Set<String> getDataObjectKeys();
	
}
