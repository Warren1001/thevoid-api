package com.kabryxis.thevoid.api.util.round;

import com.kabryxis.thevoid.api.game.GamePlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PointManager {
	
	private final Map<GamePlayer, Integer> points = new HashMap<>();
	private final Map<GamePlayer, Long> pointsAchieved = new HashMap<>();
	
	private boolean setLevel;
	
	public PointManager(boolean setLevel) {
		setLevel(setLevel);
	}
	
	public void setLevel(boolean setLevel) {
		this.setLevel = setLevel;
	}
	
	public boolean setLevel() {
		return setLevel;
	}
	
	public void reset() {
		points.clear();
		pointsAchieved.clear();
	}
	
	public void setPoints(GamePlayer gamePlayer, int p) {
		points.put(gamePlayer, p);
		pointsAchieved.put(gamePlayer, System.currentTimeMillis());
		if(setLevel) gamePlayer.setLevel(p);
	}
	
	public int getPoints(GamePlayer gamePlayer) {
		return points.getOrDefault(gamePlayer, 0);
	}
	
	public void incrementPoints(GamePlayer gamePlayer) {
		setPoints(gamePlayer, getPoints(gamePlayer) + 1);
	}
	
	public void decrementPoints(GamePlayer gamePlayer) {
		int p = getPoints(gamePlayer) - 1;
		if(p >= 0) setPoints(gamePlayer, p);
	}
	
	public List<GamePlayer> getTopGamers() { // TODO sort by time achieved
		List<GamePlayer> gamePlayers = new ArrayList<>();
		int topPoints = 0;
		for(Integer points : points.values()) {
			if(points > topPoints) topPoints = points;
		}
		for(Map.Entry<GamePlayer, Integer> entry : points.entrySet()) {
			if(entry.getValue() == topPoints) gamePlayers.add(entry.getKey());
		}
		return gamePlayers;
	}
	
	public GamePlayer getTopGamer() {
		GamePlayer topGamePlayer = null;
		int topPoints = 0;
		long topTimeAchieved = 0L;
		for(Map.Entry<GamePlayer, Integer> entry : points.entrySet()) {
			GamePlayer gamePlayer = entry.getKey();
			int points = entry.getValue();
			long timeAchieved = pointsAchieved.get(gamePlayer);
			if(points > topPoints || (points == topPoints && timeAchieved < topTimeAchieved)) {
				topGamePlayer = gamePlayer;
				topPoints = points;
				topTimeAchieved = timeAchieved;
			}
		}
		return topPoints == 0 ? null : topGamePlayer;
	}

}
