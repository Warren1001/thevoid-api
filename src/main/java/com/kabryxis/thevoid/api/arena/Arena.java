package com.kabryxis.thevoid.api.arena;

import com.kabryxis.kabutils.random.weighted.conditional.ConditionalWeighted;
import com.kabryxis.thevoid.api.arena.object.ArenaDataObjectRegistry;
import com.kabryxis.thevoid.api.arena.schematic.ArenaData;
import com.kabryxis.thevoid.api.arena.schematic.BaseArenaData;
import com.kabryxis.thevoid.api.arena.schematic.BaseSchematic;
import com.kabryxis.thevoid.api.arena.schematic.Schematic;
import com.sk89q.worldedit.EditSession;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.List;

public interface Arena extends ConditionalWeighted<Object> {
	
	String getName();
	
	Location getLocation();
	
	World getWorld();
	
	ArenaDataObjectRegistry getRegistry();
	
	EditSession getEditSession();
	
	void queueSchematics(List<? extends BaseSchematic> list);
	
	BaseArenaData getCurrentArenaData();
	
	ArenaData getArenaData(Schematic schematic);
	
	ArenaData loadAnotherSchematic(Schematic schematic);
	
	ArenaData loadAnotherSchematic(Schematic schematic, int offsetX, int offsetY, int offsetZ);
	
	void nextSchematic();
	
	void loadSchematic();
	
	void eraseSchematic();
	
	void spawnedEntity(Entity entity);
	
	void endOfRound();
	
}
