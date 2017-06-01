package com.cloudcanards.io.loading;

/**
 * Does stuff
 *
 * @author creativitRy
 */
public abstract class AbstractTask
{
	protected ResourceManager resourceManager;
	
	public AbstractTask(ResourceManager resourceManager)
	{
		this.resourceManager = resourceManager;
	}
	
	/**
	 * Checks if run() should be run in a new thread
	 *
	 * @return true if new thread should be used
	 */
	public boolean runInNewThread()
	{
		return true;
	}
	
	/**
	 * This will run in a new thread
	 */
	public abstract void run();
	
	/**
	 * Use Gdx.app.postRunnable from run() to post notice that this task is over
	 *
	 * @return true if done
	 * @see <a href="https://github.com/libgdx/libgdx/wiki/Threading">https://github.com/libgdx/libgdx/wiki/Threading</a>
	 */
	public abstract boolean isDone();
}