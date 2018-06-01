package com.kabryxis.thevoid.api.impl.round;

import com.kabryxis.thevoid.api.game.Game;
import com.kabryxis.thevoid.api.game.GamePlayer;
import com.kabryxis.thevoid.api.round.BasicRound;
import com.kabryxis.thevoid.api.round.RoundManager;

import java.util.List;

public abstract class SurvivalRound extends BasicRound {
	
	public SurvivalRound(RoundManager roundManager, String name, boolean groundInteraction) {
		super(roundManager, name, groundInteraction);
	}
	
	@Override
	public List<? extends GamePlayer> getRoundWinners(Game game) {
		return game.getPlayerManager().getAlivePlayers();
	}
	
}
