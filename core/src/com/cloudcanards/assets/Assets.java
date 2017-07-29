package com.cloudcanards.assets;

import com.cloudcanards.util.Logger;

import java.lang.reflect.Field;

/**
 * Constants for directories of assets
 *
 * @author creativitRy
 */
public class Assets
{
	public static final String DIR = "data/cloudCanards/";
	public static final String ITEM_DIR = "items/";
	public static final String SHADER_DIR = "shaders/";
	
	public static final String BADLOGIC = "badlogic.jpg";
	@Temporary
	public static final String THROBBER = "ui/cloudLoad.atlas";
	public static final String DEFAULT_INPUT_FILE_NAME = "inputs/defaultInputs.json";
	@Temporary
	public static final String ROPE = "grapple/rope.atlas";
	@Temporary
	public static final String TARGET = "grapple/target.png";
	@Temporary
	public static final String SKIN = "ui/vis/uiskin.json";
	public static final String FONT = "ui/poiretOne.ttf";
	public static final String TIMER_SHADER = "Timer.frag";
	public static final String VERTEX_SHADER = "VertexShader.vert";
	public static final String LEVEL_DIR = "levels/";
	
	/**
	 * Checks whether temporary assets are being used and logs them
	 */
	public static void checkTempAssets()
	{
		Logger.log("Checking for temporary assets...");
		Logger.setLevel(Logger.ERROR);
		for (Field field : Assets.class.getFields())
		{
			if (field.isAnnotationPresent(Temporary.class))
			{
				Logger.log("The asset " + field.getName() + " is temporary!");
			}
		}
		Logger.resetLevel();
	}
	
	private Assets() {}
}
