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
	
	public boolean runInNewThread()
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
	public boolean isDone()
	{
		return resourceManager.getAssetManager().update();
	}
}
