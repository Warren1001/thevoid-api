package com.kabryxis.thevoid.api.util.game;

import com.kabryxis.kabutils.string.IncrementalString;
import com.kabryxis.thevoid.api.game.Game;
import com.kabryxis.thevoid.api.impl.game.VoidPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.Map;

public class Board {
	
	private final Map<VoidPlayer, BoardEntry> boardEntries = new HashMap<>();
	
	private final Game game;
	private final IncrementalString roundEntry;
	private final Scoreboard board;
	
	private Objective objective;
	
	public Board(Game game) {
		this.game = game;
		this.roundEntry = new IncrementalString(ChatColor.GOLD + "Round [x]", 1);
		this.board = Bukkit.getScoreboardManager().getNewScoreboard();
		reset();
	}
	
	public Scoreboard getScoreboard() {
		return board;
	}
	
	public Objective getObjective() {
		return objective;
	}
	
	public BoardEntry getBoardEntry(VoidPlayer voidPlayer) {
		return boardEntries.computeIfAbsent(voidPlayer, p -> new BoardEntry(this, p));
	}
	
	public void reset() {
		this.objective = board.getObjective("thevoid-points");
		if(objective != null) objective.unregister();
		this.objective = board.registerNewObjective("thevoid-points", "dummy");
		objective.setDisplayName(ChatColor.BOLD.toString() + ChatColor.GREEN + "TheVoid Test");
		objective.setDisplaySlot(DisplaySlot.SIDEBAR);
		objective.getScore(ChatColor.RESET.toString()).setScore(-1);
		objective.getScore(roundEntry.reset()).setScore(-2);
		boardEntries.values().forEach(entry -> {
			entry.updateObjective();
			entry.updateScore();
		});
	}
	
	public void nextRound() {
		board.resetScores(roundEntry.get());
		objective.getScore(roundEntry.increment()).setScore(-2);
	}
	
	public void start() {
		boardEntries.values().forEach(BoardEntry::showScoreboard);
	}
	
}
