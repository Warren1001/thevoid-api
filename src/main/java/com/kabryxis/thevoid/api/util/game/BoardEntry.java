package com.kabryxis.thevoid.api.util.game;

import com.kabryxis.thevoid.api.impl.game.VoidPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class BoardEntry {
	
	private static final Scoreboard EMPTY = Bukkit.getScoreboardManager().getNewScoreboard();
	
	private final Board board;
	private final Scoreboard scoreboard;
	private final VoidPlayer voidPlayer;
	
	private Objective objective;
	private Score aliveScore, deadScore;
	
	public BoardEntry(Board board, VoidPlayer voidPlayer) {
		this.board = board;
		this.scoreboard = board.getScoreboard();
		this.voidPlayer = voidPlayer;
	}
	
	public void updateObjective() {
		this.objective = board.getObjective();
		String name = voidPlayer.getName();
		this.aliveScore = objective.getScore(ChatColor.GREEN + name);
		this.deadScore = objective.getScore(ChatColor.DARK_GRAY + name);
	}
	
	public void updateScore() {
		if(voidPlayer.isAlive()) {
			scoreboard.resetScores(deadScore.getEntry());
			aliveScore.setScore(voidPlayer.getPoints());
		}
		else {
			scoreboard.resetScores(aliveScore.getEntry());
			deadScore.setScore(voidPlayer.getPoints());
		}
	}
	
	public void showScoreboard() {
		voidPlayer.getPlayer().setScoreboard(scoreboard);
	}
	
	public void hideScoreboard() {
		voidPlayer.getPlayer().setScoreboard(EMPTY);
	}
	
}
