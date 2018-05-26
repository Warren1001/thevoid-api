package com.kabryxis.thevoid.api.round;

import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.arena.schematic.Schematic;

import java.util.ArrayList;
import java.util.List;

public interface RoundInfoRegistry<A extends Arena, S extends Schematic, R extends Round> {
	
	void registerArena(A arena);
	
	default void registerArenas(A... arenas) {
		for(A arena : arenas) {
			registerArena(arena);
		}
	}
	
	void registerSchematic(S schematic);
	
	default void registerSchematics(S... schematics) {
		for(S schematic : schematics) {
			registerSchematic(schematic);
		}
	}
	
	void registerRound(R round);
	
	default void registerRounds(R... rounds) {
		for(R round : rounds) {
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
