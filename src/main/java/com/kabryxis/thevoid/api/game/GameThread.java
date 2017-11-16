package com.kabryxis.thevoid.api.game;

import com.kabryxis.kabutils.concurrent.thread.PausableThread;

public class GameThread extends PausableThread {
	
	private final String name;
	
	private Game game;
	private boolean hasStarted = false;
	
	public GameThread(String name) {
		super(name + " - Game thread");
		this.name = name;
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
	public void run() {
		hasStarted = true;
		game.threadStart();
		while(game.canRun()) {
			if(!pause0()) break;
			game.next();
			game.start();
			game.timer();
			game.end();
		}
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
