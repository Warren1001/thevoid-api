package com.kabryxis.thevoid.api.round;

import java.util.List;

import org.bukkit.event.Event;

import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.game.Game;
import com.kabryxis.thevoid.api.game.Gamer;

public interface Round {
	
	void load(Game game, Arena arena);
	
	void start(Game game, Arena arena);
	
	void end(Game game, Arena arena);
	
	void event(Game game, Event event);
	
	void fell(Game game, Gamer gamer);
	
	String getName();
	
	int getRoundLength();
	
	List<String> getWorldNames();
	
	List<String> getSchematics();
	
	void customTimer();
	
}
