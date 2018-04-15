package com.kabryxis.thevoid.api.game;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class VoidGameGenerator extends ChunkGenerator {
	
	@Override
	public List<BlockPopulator> getDefaultPopulators(World world) {
		return Collections.emptyList();
	}
	
	@Override
	public boolean canSpawn(World world, int x, int z) {
		return true;
	}
	
	@Override
	public byte[][] generateBlockSections(World world, Random random, int cx, int cz, BiomeGrid biomes) {
		byte[][] result = new byte[world.getMaxHeight() / 16][];
		if(world.getEnvironment() == World.Environment.NETHER) {
			for(int x = 0; x < 16; x++) {
				for(int y = 0; y < 8; y++) {
					for(int z = 0; z < 16; z++) {
						setBlock(result, x, y, z, (byte)11);
					}
				}
			}
		}
		return result;
	}
	
	@Override
	public Location getFixedSpawnLocation(World world, Random random) {
		return new Location(world, 0, 100, 0);
	}
	
	public void setBlock(byte[][] result, int x, int y, int z, byte id) {
		if(result[y >> 4] == null) result[y >> 4] = new byte[4096];
		result[y >> 4][((y & 0xf) << 8) | (z << 4) | x] = id;
	}
	
}
