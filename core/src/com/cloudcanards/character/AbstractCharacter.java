package com.cloudcanards.character;

import com.cloudcanards.behavior.Renderable;
import com.cloudcanards.behavior.Updateable;
import com.cloudcanards.io.loading.Disposable;
import com.cloudcanards.io.loading.Loadable;
import com.cloudcanards.io.loading.ResourceManager;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Character
 *
 * @author creativitRy
 */
public abstract class AbstractCharacter implements Loadable, Updateable, Renderable, Disposable
{
	public AbstractCharacter()
	{
	
	}
	
	@Override
	public void load(ResourceManager resourceManager)
	{
	
	}
	
	@Override
	public void update(float delta)
	{
	
	}
	
	@Override
	public void render(SpriteBatch batch, float delta)
	{
	
	}
	
	@Override
	public void dispose(ResourceManager resourceManager)
	{
	
	}
}
