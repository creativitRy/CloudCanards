package com.cloudcanards;

import com.cloudcanards.io.loading.ResourceManager;
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
	public static CloudCanards instance;
	
	public static CloudCanards getInstance()
	{
		return instance;
	}
	
	private ResourceManager resourceManager;
	
	/**
	 * Don't put anything here. Put it in create() instead
	 */
	public CloudCanards()
	{
		instance = this;
	}
	
	@Override
	public void create()
	{
		resourceManager = new ResourceManager();
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
			float deltaTime = Gdx.graphics.getDeltaTime();
			if (deltaTime > 1)
				deltaTime = 1;
			screen.render(deltaTime);
		}
	}
	
	@Override
	public void dispose()
	{
		resourceManager.dispose();
	}
}
