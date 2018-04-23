package com.kabryxis.thevoid.api.arena.schematic;

import com.kabryxis.thevoid.api.arena.schematic.util.SchematicEntry;
import com.kabryxis.thevoid.api.arena.schematic.util.SchematicWork;

import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public interface ISchematic {
	
	String getName();
	
	List<SchematicEntry> getSchematicEntries();
	
	double getSizeX();
	
	double getSizeZ();
	
	void addSchematicWork(Supplier<? extends SchematicWork> clazz);
	
	boolean hasSchematicWork();
	
	Set<Supplier<? extends SchematicWork>> getSchematicWork();
	
}
