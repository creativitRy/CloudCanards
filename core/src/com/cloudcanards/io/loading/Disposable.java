package com.cloudcanards.io.loading;

/**
 * Disposeable
 *
 * @author creativitRy
 */
public interface Disposable
{
	/**
	 * Unload assets from assetmanager. Called when screen is disposed
	 *
	 * @param resourceManager call getAssetManager()
	 */
	void dispose(ResourceManager resourceManager);
}
