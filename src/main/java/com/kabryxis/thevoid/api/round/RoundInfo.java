package com.kabryxis.thevoid.api.round;

import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.game.Game;
import com.kabryxis.thevoid.api.schematic.Schematic;

public interface RoundInfo {
	
	public Round getRound();
	
	public Arena getArena();
	
	public Schematic getSchematic();
	
	public void load(Game game);
	
}
