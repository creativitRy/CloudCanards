package com.cloudcanards.loading;

/**
 * Loadable
 *
 * @author creativitRy
 */
public interface Loadable
{
	/**
	 * {@link AbstractTask} for multithreaded operations, {@link AbstractLoadAssetTask} for loading assets
	 *
	 * @param resourceManager call addTask
	 */
	void load(ResourceManager resourceManager);
}
