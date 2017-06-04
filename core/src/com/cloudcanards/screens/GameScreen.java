package com.cloudcanards.screens;

import com.cloudcanards.assets.Assets;
import com.cloudcanards.box2d.MapCollisionBuilderTask;
import com.cloudcanards.io.loading.AbstractLoadAssetTask;
import com.cloudcanards.io.loading.AbstractTask;
import com.cloudcanards.io.loading.ResourceManager;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * GameScreen
 *
 * @author creativitRy
 */
public class GameScreen extends AbstractScreen
{
	public static final Vector2 GRAVITY = new Vector2(0, -10);
	private String mapName;
	private TiledMap map;
	
	private World world;
	
	public GameScreen(String mapName)
	{
		this.mapName = mapName;
	}
	
	@Override
	public void load(ResourceManager resourceManager)
	{
		//init world
		resourceManager.addTasks(new AbstractTask(resourceManager)
		{
			@Override
			public void run()
			{
				world = new World(GRAVITY, true);
				finish();
			}
		});
		
		//load map
		resourceManager.addTasks(new AbstractLoadAssetTask(resourceManager)
		{
			@Override
			public void run()
			{
				resourceManager.getAssetManager().load(Assets.DIR + mapName, TiledMap.class);
			}
			
			@Override
			public void postRun()
			{
				map = resourceManager.getAssetManager().get(Assets.DIR + mapName);
				
				MapLayer collisionLayer = map.getLayers().get("collision");
				if (collisionLayer != null)
				{
					resourceManager.addTasks(new MapCollisionBuilderTask(resourceManager,
						map.getProperties().get("tilewidth", 32, Integer.class),
						collisionLayer, world));
				}
			}
		});
	}
	
	@Override
	public void show()
	{
	
	}
	
	@Override
	public void render(float delta)
	{
	
	}
	
	@Override
	public void resize(int width, int height)
	{
	
	}
	
	@Override
	public void pause()
	{
	
	}
	
	@Override
	public void resume()
	{
	
	}
	
	@Override
	public void dispose(ResourceManager manager)
	{
	
	}
}
