package com.kabryxis.thevoid.api.round;

import com.kabryxis.kabutils.spigot.data.Config;

public interface RoundManager<T extends Round> {
	
	void registerRound(T round);
	
	void registerRounds(T... rounds);
	
	void addGlobalRequiredDefault(String key, Object object);
	
	void addGlobalRequiredObject(String key, Class<?> objectType);
	
	Round getRandomRound();
	
	Config getData(T round);
	
}
