package com.cloudcanards.desktop;

import com.cloudcanards.CloudCanards;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

/**
 * DesktopLauncher
 *
 * @author creativitRy
 */
public class DesktopLauncher
{
	public static void main(String[] arg)
	{
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		config.title = "Cloud Canards";
		
		config.width = 1600;
		config.height = 900;
		//todo: to change antialiasing settings, restart and load this from preferences
		config.samples = 0; //antialiasing
		//Sets the window to borderless window
		/*System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
		config.width = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
		config.height = LwjglApplicationConfiguration.getDesktopDisplayMode().height;*/
		
		new LwjglApplication(CloudCanards.getInstance(), config);
	}
}
