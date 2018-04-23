package com.kabryxis.thevoid.api.arena.object.impl;

import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.arena.object.ArenaDataObject;
import com.kabryxis.thevoid.api.arena.object.ArenaDataObjectable;
import com.kabryxis.thevoid.api.arena.object.BiSupplier;
import com.kabryxis.thevoid.api.arena.object.IArenaDataObjectRegistry;
import com.kabryxis.thevoid.api.arena.schematic.IBaseSchematic;

import java.util.HashMap;
import java.util.Map;

public class VoidArenaDataObjectRegistry implements IArenaDataObjectRegistry {
	
	private final Map<String, BiSupplier<? extends ArenaDataObject, ? super IBaseSchematic, ? super Arena>> dataObjectCreators = new HashMap<>();
	private final Map<String, Class<? extends ArenaDataObject>> classes = new HashMap<>();
	
	@Override
	public <T extends ArenaDataObject> void register(String key, Class<T> clazz, BiSupplier<T, ? super IBaseSchematic, ? super Arena> supplier) {
		classes.put(key, clazz);
		dataObjectCreators.put(key, supplier);
	}
	
	@Override
	public void handle(ArenaDataObjectable dataObjectable) {
		for(String key : dataObjectable.getDataObjectKeys()) {
			BiSupplier<? extends ArenaDataObject, ? super IBaseSchematic, ? super Arena> supplier = dataObjectCreators.get(key);
			if(supplier == null || dataObjectable.hasDataObject(classes.get(key))) continue;
			dataObjectable.registerDataObject(supplier.get(dataObjectable.getSchematic(), dataObjectable.getArena()));
		}
	}
	
}
