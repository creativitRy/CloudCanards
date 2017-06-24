package com.cloudcanards.graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Renderable
 *
 * @author creativitRy
 */
public interface Renderable
{
	void render(SpriteBatch batch, float delta);
	
	/**
	 * Order of rendering. -1 is rendered before 1. If changed, call
	 * {@linkplain com.cloudcanards.graphics.RenderableManager#markZOrderChanged(Renderable) markZOrderChanged}
	 *
	 * @return z (depth) order
	 */
	default int getZOrder()
	{
		return 0;
	}
}
