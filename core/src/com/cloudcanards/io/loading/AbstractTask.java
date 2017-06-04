package com.cloudcanards.io.loading;

/**
 * Does stuff
 *
 * @author creativitRy
 */
public abstract class AbstractTask
{
	protected ResourceManager resourceManager;
	private boolean done;
	
	public AbstractTask(ResourceManager resourceManager)
	{
		this.resourceManager = resourceManager;
		done = false;
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
	 * This will run in a new thread. Call finish() after finished
	 */
	public abstract void run();
	
	protected void finish()
	{
		done = true;
	}
	
	/**
	 * Use Gdx.app.postRunnable from run() to post notice that this task is over
	 *
	 * @return true if done
	 * @see <a href="https://github.com/libgdx/libgdx/wiki/Threading">https://github.com/libgdx/libgdx/wiki/Threading</a>
	 */
	public boolean isDone()
	{
		return done;
	}
	
}