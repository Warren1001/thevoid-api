package com.kabryxis.thevoid.api.round;

import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.schematic.BaseSchematic;

import java.util.List;

public interface RoundInfoRegistry {
	
	void registerArena(Arena arena);
	
	void registerSchematic(BaseSchematic schematic);
	
	void registerRound(Round round);
	
	void registerRounds(Round... rounds);
	
	void queueArenaData(List<RoundInfo> infos, int amount);
	
}
