package com.cloudcanards.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.cloudcanards.CloudCanards;

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
		//Sets the window to borderless window
		/*System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
		config.width = LwjglApplicationConfiguration.getDesktopDisplayMode().width;
		config.height = LwjglApplicationConfiguration.getDesktopDisplayMode().height;*/
		
		new LwjglApplication(new CloudCanards(), config);
	}
}
