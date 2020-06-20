package procedural.biome.types.dry;

import java.util.Random;

import map.Moisture;
import map.Temperature;
import procedural.NoiseUtil;
import procedural.biome.Biome;
import procedural.biome.types.BiomeColors;
import procedural.structures.Structure;

public class DesertBiome extends Biome {
	public DesertBiome() {
		////////////////////////////////////////////////
		this.name = "Desert";
		this.temp = Temperature.HOT;
		this.moisture = Moisture.DRY;
		this.terrainHeightFactor = 2f;
		this.terrainRoughness = 8f;
		
		this.soilQuality = .1f;
		
		this.skyColor = BiomeColors.DESERT_SKY;
		this.groundColor = BiomeColors.SAND_COLOR;
		////////////////////////////////////////////////
	}

	@Override
	public float augmentTerrainHeight(int x, int z, float currentHeight, int subseed, Random r) {
		return currentHeight;
	}
	
	@Override
	public int getTerrainTileItems(int x, int z, float currentHeight, int subseed, Random r, int[][] tileItems) {
		int tile = r.nextInt(520);
		switch(tile) { 
		case 0: return 4;
		case 1: return 7;
		case 2: return 8;
		}
		return 0;
	}
	
	@Override
	public float getWaterTable(int x, int z, float height, int subseed) {
		return Float.MIN_VALUE;
	}
	
	@Override
	public Structure getTerrainStructures(int x, int z, float currentHeight, int subseed, Random r) {
		return null;
	}
}