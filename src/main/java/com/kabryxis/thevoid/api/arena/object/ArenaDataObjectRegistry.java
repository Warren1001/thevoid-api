package com.kabryxis.thevoid.api.arena.object;

import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.arena.BaseSchematicData;
import com.kabryxis.thevoid.api.schematic.BaseSchematic;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class ArenaDataObjectRegistry {
	
	private final Map<String, Constructor<? extends ArenaDataObject>> dataObjectCreators = new HashMap<>();
	
	public void registerDataObjectCreator(String key, Class<? extends ArenaDataObject> clazz) {
		Constructor<? extends ArenaDataObject> constructor;
		try {
			constructor = clazz.getConstructor(BaseSchematic.class, Arena.class);
			constructor.setAccessible(true);
		}
		catch(NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			return;
		}
		dataObjectCreators.put(key, constructor);
	}
	
	public void handle(BaseSchematicData data) {
		BaseSchematic schematic = data.getSchematic();
		for(String key : schematic.getData().getKeys(true)) {
			Constructor<? extends ArenaDataObject> creator = dataObjectCreators.get(key);
			if(creator == null) continue;
			if(data.hasDataObject(creator.getDeclaringClass())) continue;
			try {
				data.registerDataObject(creator.newInstance(schematic, data.getArena()));
			}
			catch(InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
	}
	
}
