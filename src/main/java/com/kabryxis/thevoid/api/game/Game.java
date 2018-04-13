package com.kabryxis.thevoid.api.game;

import com.kabryxis.thevoid.api.round.RoundInfo;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import java.util.function.Consumer;
import java.util.logging.Logger;

public interface Game {
	
	String getName();
	
	Plugin getOwner();
	
	Logger getLogger();
	
	void threadStart();
	
	boolean canRun();
	
	void next();
	
	void start();
	
	void timer();
	
	void end();
	
	void threadEnd();
	
	void pause();
	
	void unpause();
	
	void forEachGamer(Consumer<? super Gamer> action);
	
	RoundInfo getCurrentRoundInfo();
	
	void callEvent(Event event);
	
	boolean isInProgress();
	
	void addGamer(Gamer gamer);
	
	void removeGamer(Gamer gamer);
	
	void died(Gamer gamer);
	
}
