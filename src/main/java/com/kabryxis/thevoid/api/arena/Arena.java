package com.kabryxis.thevoid.api.arena;

import com.kabryxis.thevoid.api.arena.object.IArenaDataObjectRegistry;
import com.kabryxis.thevoid.api.arena.schematic.ArenaData;
import com.kabryxis.thevoid.api.arena.schematic.BaseArenaData;
import com.kabryxis.thevoid.api.arena.schematic.IBaseSchematic;
import com.kabryxis.thevoid.api.arena.schematic.ISchematic;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.List;

public interface Arena {
	
	String getName();
	
	Location getLocation();
	
	default World getWorld() {
		return getLocation().getWorld();
	}
	
	default String getWorldName() {
		return getWorld().getName();
	}
	
	IArenaDataObjectRegistry getRegistry();
	
	void queueSchematics(List<? extends IBaseSchematic> list);
	
	BaseArenaData getCurrentArenaData();
	
	ArenaData getArenaData(ISchematic schematic);
	
	ArenaData loadAnotherSchematic(ISchematic schematic);
	
	void nextSchematic();
	
	void loadSchematic();
	
	void eraseSchematic();
	
	void spawnedEntity(Entity entity);
	
	void endOfRound();
	
}
