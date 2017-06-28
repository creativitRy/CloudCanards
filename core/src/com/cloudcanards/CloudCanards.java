package com.cloudcanards;

import com.cloudcanards.input.InputManager;
import com.cloudcanards.loading.ResourceManager;
import com.cloudcanards.screens.GameScreen;
import com.cloudcanards.screens.LoadingScreen;
import com.cloudcanards.util.Logger;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.physics.box2d.Box2D;

/**
 * CloudCanards
 *
 * @author creativitRy
 */
public class CloudCanards extends Game
{
	private static final CloudCanards INSTANCE = new CloudCanards();
	private static final float MAX_DELTA_TIME = 1f / 20f; //20 fps or below slows down the game instead of skipping frames
	
	public static CloudCanards getInstance()
	{
		return INSTANCE;
	}
	
	private ResourceManager resourceManager;
	
	/**
	 * Don't put anything here. Put it in create() instead
	 */
	private CloudCanards() {}
	
	@Override
	public void create()
	{
		resourceManager = new ResourceManager();
		//todo: move to loading screen after splash screen - make sure this is called before getInstance()
		InputManager.init();
		//todo: move to starting game
		Box2D.init();
		
		setScreen(new LoadingScreen(new GameScreen("levels/dev/steampunk.tmx")));
	}
	
	public ResourceManager getResourceManager()
	{
		return resourceManager;
	}
	
	@Override
	public void setScreen(Screen screen)
	{
		super.setScreen(screen);
		Logger.log("Setting screen to " + screen.getClass().getSimpleName());
	}
	
	@Override
	public void render()
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//super.render();
		if (screen != null)
		{
			screen.render(Math.min(Gdx.graphics.getDeltaTime(), MAX_DELTA_TIME));
		}
	}
	
	@Override
	public void dispose()
	{
		resourceManager.dispose();
	}
}
