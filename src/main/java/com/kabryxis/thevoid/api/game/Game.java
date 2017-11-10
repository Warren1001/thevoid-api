package com.kabryxis.thevoid.api.game;

import java.util.function.Consumer;
import java.util.logging.Logger;

import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import com.kabryxis.thevoid.api.round.RoundInfo;

public interface Game {
	
	public String getName();
	
	public Plugin getOwner();
	
	public Logger getLogger();
	
	public void threadStart();
	
	public boolean canRun();
	
	public void next();
	
	public void start();
	
	public void timer();
	
	public void end();
	
	public void threadEnd();
	
	public void pause();
	
	public void unpause();
	
	public void forEachGamer(Consumer<? super Gamer> action);
	
	public RoundInfo getCurrentRoundInfo();
	
	public void callEvent(Event event);
	
	public boolean isInProgress();
	
	public void addGamer(Gamer gamer);
	
	public void removeGamer(Gamer gamer);
	
}
