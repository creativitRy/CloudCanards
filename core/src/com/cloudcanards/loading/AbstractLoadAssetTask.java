package com.cloudcanards.loading;

/**
 * Loads assets
 *
 * @author creativitRy
 */
public abstract class AbstractLoadAssetTask extends AbstractTask
{
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
	
	@Override
	protected final void finish()
	{
		super.finish();
	}
	
	/**
	 * Runs once after isDone returns true.
	 * Does nothing if not overwritten
	 */
	public void postRun()
	{
	
	}
}
