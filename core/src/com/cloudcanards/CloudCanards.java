package com.cloudcanards;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.cloudcanards.io.ResourceManager;

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
	
	public CloudCanards()
	{
		instance = this;
	}
	
	@Override
	public void create()
	{
		resourceManager = new ResourceManager();
	}
	
	@Override
	public void render()
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		super.render();
	}
	
	@Override
	public void dispose()
	{
		resourceManager.dispose();
	}
}
