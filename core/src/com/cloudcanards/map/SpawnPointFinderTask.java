package com.cloudcanards.map;

import com.cloudcanards.loading.AbstractTask;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * SpawnPointFinderTask
 *
 * @author creativitRy
 */
public class SpawnPointFinderTask extends AbstractTask
{
	private final Array<Vector2> spawnPoints;
	private float tileSize;
	private TiledMapTileLayer layer;
	
	public SpawnPointFinderTask(float tileSize, MapLayer layer, Array<Vector2> spawnPoints)
	{
		this.tileSize = tileSize;
		if (!(layer instanceof TiledMapTileLayer))
		{
			throw new IllegalArgumentException("The map's layer is not a tile layer");
		}
		this.layer = (TiledMapTileLayer) layer;
		
		this.spawnPoints = spawnPoints;
	}
	
	@Override
	public void run()
	{
		for (int h = 0; h < layer.getHeight(); h++)
		{
			for (int w = 0; w < layer.getWidth(); w++)
			{
				//todo: change this to reflections calling method by the tile id if the id is part of an array
				if (layer.getCell(w, h) != null && layer.getCell(w, h).getTile() != null)
				{
					onSpawnTile(w, h, layer.getCell(w, h));
				}
				
			}
		}
		
		finish();
	}
	
	private void onSpawnTile(int w, int h, TiledMapTileLayer.Cell cell)
	{
		spawnPoints.add(new Vector2(w, h));
	}
}
