package com.cloudcanards.map;

import com.cloudcanards.grapple.StaticTarget;
import com.cloudcanards.grapple.Targetable;
import com.cloudcanards.loading.AbstractTask;
import com.cloudcanards.screens.GameScreen;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

/**
 * SpecialTilesBuilderTask
 *
 * @author creativitRy
 */
public class SpecialTilesBuilderTask extends AbstractTask
{
	private float tileSize;
	private TiledMapTileLayer layer;
	private World world;
	
	private Array<Targetable> grappleTargets;
	
	public SpecialTilesBuilderTask(float tileSize, MapLayer layer, World world)
	{
		this.tileSize = tileSize;
		if (!(layer instanceof TiledMapTileLayer))
		{
			throw new IllegalArgumentException("The map's \"speicial\" layer is not a tile layer");
		}
		this.layer = (TiledMapTileLayer) layer;
		this.world = world;
		
		grappleTargets = new Array<>();
	}
	
	@Override
	public void run()
	{
		for (int h = 0; h < layer.getHeight(); h++)
		{
			for (int w = 0; w < layer.getWidth(); w++)
			{
				//todo: change this to reflections calling method by the tile id if the id is part of an array
				if (layer.getCell(w, h) != null && layer.getCell(w, h).getTile() != null &&
					layer.getCell(w, h).getTile().getId() == 157)
				{
					onGrappleTile(w, h, layer.getCell(w, h));
				}
				
			}
		}
		
		GameScreen.getInstance().getStaticGrappleTargets().addAll(grappleTargets);
		GameScreen.getInstance().getStaticGrappleTargets().init();
		
		finish();
	}
	
	private void onGrappleTile(int w, int h, TiledMapTileLayer.Cell cell)
	{
		//layer.setCell(w, h, null);
		
		grappleTargets.add(new StaticTarget(w + 0.5f, h + 0.5f, world));
	}
}
