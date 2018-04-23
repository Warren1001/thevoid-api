package com.kabryxis.thevoid.api.arena.object;

@FunctionalInterface
public interface BiSupplier<R, T, U> {
	
	R get(T t, U u);
	
}
