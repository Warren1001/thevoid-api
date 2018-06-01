package com.kabryxis.thevoid.api.util.game;

import com.kabryxis.thevoid.api.game.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class BoardEntry {
	
	private static final Scoreboard EMPTY = Bukkit.getScoreboardManager().getNewScoreboard();
	
	private final Board board;
	private final Scoreboard scoreboard;
	private final GamePlayer gamePlayer;
	
	private Objective objective;
	private Score aliveScore, deadScore;
	
	public BoardEntry(Board board, GamePlayer gamePlayer) {
		this.board = board;
		this.scoreboard = board.getScoreboard();
		this.gamePlayer = gamePlayer;
	}
	
	public void updateObjective() {
		this.objective = board.getObjective();
		String name = gamePlayer.getName();
		this.aliveScore = objective.getScore(ChatColor.GREEN + name);
		this.deadScore = objective.getScore(ChatColor.DARK_GRAY + name);
	}
	
	public void updateScore() {
		if(gamePlayer.isAlive()) {
			scoreboard.resetScores(deadScore.getEntry());
			aliveScore.setScore(gamePlayer.getPoints());
		}
		else {
			scoreboard.resetScores(aliveScore.getEntry());
			deadScore.setScore(gamePlayer.getPoints());
		}
	}
	
	public void showScoreboard() {
		gamePlayer.getPlayer().setScoreboard(scoreboard);
	}
	
	public void hideScoreboard() {
		gamePlayer.getPlayer().setScoreboard(EMPTY);
	}
	
}
