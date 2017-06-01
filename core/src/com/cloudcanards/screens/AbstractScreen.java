package com.cloudcanards.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.cloudcanards.CloudCanards;
import com.cloudcanards.io.loading.ResourceManager;

/**
 * AbstractScreen
 *
 * @author creativitRy
 */
public abstract class AbstractScreen implements Screen
{
	public static final float SCREEN_WIDTH = 1600;
	public static final float SCREEN_HEIGHT = 900;
	
	public abstract void load(ResourceManager resourceManager);
	
	/**
	 * Called when the screen should render itself. GL stuff are cleared already.
	 * <p>Call the methods <code>viewport.apply()</code> and <code>batch.setProjectionMatrix(camera.combined)</code>
	 * here
	 *
	 * @param delta The time in seconds since the last render.
	 */
	public abstract void render(float delta);
	
	/**
	 * Called when the {@link com.badlogic.gdx.Application} is resized. This can happen at any point during a
	 * non-paused state but will never happen before a call to create().
	 * <p>Call the methods <code>viewport.update(width, height)</code>
	 * here
	 *
	 * @param width  the new width in pixels
	 * @param height the new height in pixels
	 */
	public abstract void resize(int width, int height);
	
	/**
	 * Called when this screen is no longer the current screen for a {@link Game}.
	 * Calls dispose()
	 */
	@Override
	public void hide()
	{
		dispose();
	}
	
	@Override
	public void dispose()
	{
		dispose(CloudCanards.getInstance().getResourceManager());
	}
	
	public abstract void dispose(ResourceManager manager);
}
