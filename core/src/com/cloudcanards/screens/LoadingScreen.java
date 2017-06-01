package com.cloudcanards.screens;

import com.cloudcanards.CloudCanards;
import com.cloudcanards.io.loading.ResourceManager;

/**
 * LoadingScreen
 *
 * @author creativitRy
 */
public class LoadingScreen extends AbstractScreen
{
	private ResourceManager resourceManager;
	
	private AbstractScreen screen;
	
	public LoadingScreen(AbstractScreen screen)
	{
		resourceManager = CloudCanards.getInstance().getResourceManager();
		
		this.screen = screen;
	}
	
	@Override
	public void load(ResourceManager resourceManager)
	{
	
	}
	
	@Override
	public void show()
	{
	
	}
	
	@Override
	public void render(float delta)
	{
	
	}
	
	@Override
	public void resize(int width, int height)
	{
	
	}
	
	@Override
	public void pause()
	{
	
	}
	
	@Override
	public void resume()
	{
	
	}
	
	@Override
	public void dispose(ResourceManager manager)
	{
	
	}
}
