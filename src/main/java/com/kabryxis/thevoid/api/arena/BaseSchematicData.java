package com.kabryxis.thevoid.api.arena;

import com.kabryxis.thevoid.api.arena.object.ArenaDataObject;
import com.kabryxis.thevoid.api.schematic.BaseSchematic;
import com.kabryxis.thevoid.api.schematic.Schematic;
import org.bukkit.Location;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class BaseSchematicData extends SchematicData {
	
	private Map<String, ArenaDataObject> dataObjects;
	private Location center;
	
	public BaseSchematicData(Schematic schematic, Arena arena) {
		super(schematic, arena);
		arena.getRegistry().handle(this);
	}
	
	public BaseSchematic getSchematic() {
		return (BaseSchematic)super.getSchematic();
	}
	
	public void setCenter(Location center) {
		this.center = center;
	}
	
	public Location getCenter() {
		return center;
	}
	
	public void registerDataObject(String name, ArenaDataObject dataObject) {
		if(dataObjects == null) dataObjects = new HashMap<>();
		dataObjects.put(name, dataObject);
	}
	
	public ArenaDataObject getDataObject(String name) {
		return dataObjects == null ? null : dataObjects.get(name);
	}
	
	public boolean hasDataObject(String name) {
		return dataObjects != null && dataObjects.get(name) != null;
	}
	
	public void forEachDataObject(Consumer<? super ArenaDataObject> action) {
		if(dataObjects != null) dataObjects.values().forEach(action);
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof BaseSchematicData)) return false;
		BaseSchematicData d = (BaseSchematicData)o;
		return d.getArena().getName().equals(getArena().getName()) && d.getSchematic().getName().equals(getSchematic().getName());
	}
	
}
