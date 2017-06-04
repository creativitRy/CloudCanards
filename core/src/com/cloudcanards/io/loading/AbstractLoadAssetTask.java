package com.cloudcanards.io.loading;

/**
 * Loads assets
 *
 * @author creativitRy
 */
public abstract class AbstractLoadAssetTask extends AbstractTask
{
	public AbstractLoadAssetTask(ResourceManager resourceManager)
	{
		super(resourceManager);
	}
	
	public final boolean runInNewThread()
	{
		return false;
	}
	
	/**
	 * Put every assetmanager.load() here
	 */
	public abstract void run();
	
	/**
	 * Updates the assets manager
	 *
	 * @return true if done
	 */
	@Override
	public final boolean isDone()
	{
		if (super.isDone())
			return true;
		
		if (resourceManager.getAssetManager().update())
		{
			finish();
			postRun();
			return true;
		}
		return false;
	}
	
	/**
	 * Runs once after isDone returns true.
	 * Does nothing if not overwritten
	 */
	public void postRun()
	{
	
	}
}
