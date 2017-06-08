package com.cloudcanards.loading;

import com.cloudcanards.assets.Assets;

import com.badlogic.gdx.graphics.Texture;

/**
 * Loads starting assets necessary to start the game
 *
 * @author creativitRy
 */
public class StartingLoadAssetTask extends AbstractLoadAssetTask
{
	@Override
	public void run()
	{
		resourceManager.getAssetManager().load(Assets.DIR + Assets.BADLOGIC, Texture.class);
	}
}
