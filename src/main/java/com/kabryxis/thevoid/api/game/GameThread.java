package com.kabryxis.thevoid.api.game;

import java.util.logging.Logger;

import com.kabryxis.kabutils.concurrent.thread.PausableThread;

public class GameThread extends PausableThread {
	
	private final String name;
	private final Logger logger;
	
	private Game game;
	private boolean hasStarted = false;
	
	public GameThread(String name) {
		super(name + " - Game thread");
		this.name = name;
		this.logger = Logger.getLogger(name);
	}
	
	public String getGameThreadName() {
		return name;
	}
	
	public void setGame(Game game) {
		if(hasStarted) {
			game.getLogger().warning("I was attempted to set to the plugin '" + getGameThreadName() + "''s GameThread while it was running.");
			return;
		}
		this.game = game;
	}
	
	public boolean hasStarted() {
		return hasStarted;
	}
	
	@Override
	public void start() {
		if(game == null) {
			logger.severe("The GameThread '" + name + "' tried to start without a Game object.");
			return;
		}
		super.start();
	}
	
	@Override
	protected void begin() {
		hasStarted = true;
		game.threadStart();
	}
	
	@Override
	protected boolean canTick() {
		return game.canRun();
	}
	
	@Override
	protected void tick() {
		game.next();
		game.start();
		game.timer();
		game.end();
	}
	
	@Override
	protected void end() {
		game.threadEnd();
	}
	
	@Override
	public void onPause() {
		game.pause();
	}
	
	@Override
	public void onUnpause() {
		game.unpause();
	}
	
}
