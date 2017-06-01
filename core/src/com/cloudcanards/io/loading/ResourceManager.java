package com.cloudcanards.io.loading;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;

import java.util.ArrayDeque;

/**
 * ResourceManager
 *
 * @author creativitRy
 */
public class ResourceManager
{
	private AssetManager assetManager;
	private ArrayDeque<AbstractTask> tasks;
	private ArrayDeque<AbstractTask> multithreadAddFixer;
	private boolean multithreadRemoveFixer;
	
	public ResourceManager()
	{
		assetManager = new AssetManager();
		assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		tasks = new ArrayDeque<>();
		multithreadAddFixer = new ArrayDeque<>();
	}
	
	public AssetManager getAssetManager()
	{
		return assetManager;
	}
	
	
	public void addTasks(AbstractTask task)
	{
		multithreadAddFixer.add(task);
	}
	
	/**
	 * @return true if done loading everything
	 */
	public boolean update()
	{
		while (!multithreadAddFixer.isEmpty())
		{
			tasks.add(multithreadAddFixer.pop());
		}
		
		if (tasks.isEmpty())
			return true;
		
		if (multithreadRemoveFixer)
		{
			tasks.remove();
			multithreadRemoveFixer = false;
			
			if (!tasks.isEmpty())
			{
				//run next task
				if (tasks.peek().runInNewThread())
				{
					new Thread(tasks.peek()::run).start();
				}
				else
					tasks.peek().run();
			}
		}
		
		if (!tasks.isEmpty() && tasks.peek().isDone())
		{
			multithreadRemoveFixer = true;
		}
		
		return false;
	}
	
	/**
	 * Disposes all assets in the manager and stops all asynchronous loading.
	 */
	public void dispose()
	{
		assetManager.dispose();
	}
}
