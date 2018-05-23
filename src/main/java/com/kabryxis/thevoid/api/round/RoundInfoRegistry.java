package com.kabryxis.thevoid.api.round;

import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.arena.schematic.BaseSchematic;

import java.util.ArrayList;
import java.util.List;

public interface RoundInfoRegistry {
	
	void registerArena(Arena arena);
	
	default void registerArenas(Arena... arenas) {
		for(Arena arena : arenas) {
			registerArena(arena);
		}
	}
	
	void registerSchematic(BaseSchematic schematic);
	
	default void registerSchematics(BaseSchematic... schematics) {
		for(BaseSchematic schematic : schematics) {
			registerSchematic(schematic);
		}
	}
	
	void registerRound(Round round);
	
	default void registerRounds(Round... rounds) {
		for(Round round : rounds) {
			registerRound(round);
		}
	}
	
	void queueArenaData(List<RoundInfo> infos, int amount);
	
	default List<RoundInfo> getArenaData(int amount) {
		List<RoundInfo> roundInfos = new ArrayList<>(amount);
		queueArenaData(roundInfos, amount);
		return roundInfos;
	}
	
}
