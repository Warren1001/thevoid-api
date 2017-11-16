package com.kabryxis.thevoid.api.round;

import java.util.List;

import org.bukkit.event.Event;

import com.kabryxis.thevoid.api.arena.Arena;
import com.kabryxis.thevoid.api.game.Game;

public interface Round {
	
	public void load(Game game, Arena arena);
	
	public void start(Game game, Arena arena);
	
	public void end(Game game, Arena arena);
	
	public void event(Game game, Event event);
	
	public String getName();
	
	public int getRoundLength();
	
	public List<String> getWorldNames();
	
	public List<String> getSchematics();
	
	public void customTimer();
	
}
