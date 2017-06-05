package com.cloudcanards.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Pool;

/**
 * RenderableComponent
 *
 * @author creativitRy
 */
public class RenderableComponent implements Component, Pool.Poolable
{
	public TextureRegion texture;
	
	@Override
	public void reset()
	{
		texture = null;
	}
}