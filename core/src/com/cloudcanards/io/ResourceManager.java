package com.cloudcanards.io;

import com.badlogic.gdx.assets.AssetManager;

/**
 * ResourceManager
 *
 * @author creativitRy
 */
public class ResourceManager
{
	private AssetManager assetManager;
	
	public ResourceManager()
	{
		assetManager = new AssetManager();
	}
	
	public AssetManager getAssetManager()
	{
		return assetManager;
	}
	
	/**
	 * Disposes all assets in the manager and stops all asynchronous loading.
	 */
	public void dispose()
	{
		assetManager.dispose();
	}
}
