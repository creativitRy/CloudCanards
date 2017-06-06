package com.cloudcanards.loading;

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
	
	
	public void addTask(AbstractTask task)
	{
		multithreadAddFixer.add(task);
	}
	
	/**
	 * @return true if done loading everything
	 */
	public boolean update()
	{
		//add tasks to the queue
		while (!multithreadAddFixer.isEmpty())
		{
			if (tasks.isEmpty())
			{
				runTask(multithreadAddFixer.peek());
			}
			
			tasks.add(multithreadAddFixer.remove());
		}
		
		//done updating
		if (tasks.isEmpty())
			return true;
		
		//remove task and run next task
		if (multithreadRemoveFixer)
		{
			tasks.remove();
			multithreadRemoveFixer = false;
			
			if (!tasks.isEmpty())
			{
				runTask(tasks.peek());
			}
		}
		
		//set task to be removed and run postrun
		if (!tasks.isEmpty() && tasks.peek().isDone())
		{
			multithreadRemoveFixer = true;
		}
		
		//still updating
		return false;
	}
	
	private void runTask(AbstractTask task)
	{
		if (task.runInNewThread())
		{
			new Thread(task::run).start();
		}
		else
			task.run();
	}
	
	/**
	 * Disposes all assets in the manager and stops all asynchronous loading.
	 */
	public void dispose()
	{
		assetManager.dispose();
	}
}
