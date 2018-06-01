package com.kabryxis.thevoid.api.game;

import com.kabryxis.thevoid.api.round.RoundInfo;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

public interface Game {
	
	String getName();
	
	Plugin getOwner();
	
	PlayerManager getPlayerManager();
	
	boolean canRun();
	
	void threadStart();
	
	void next();
	
	void start();
	
	void timer();
	
	void end();
	
	void threadEnd();
	
	void pause();
	
	void unpause();
	
	void callEvent(Event event);
	
	RoundInfo getCurrentRoundInfo();
	
	boolean isInProgress();
	
	boolean kill(GamePlayer gamePlayer);
	
	void revive(GamePlayer gamePlayer);
	
}
