package com.cloudcanards.ui;

import com.cloudcanards.CloudCanards;
import com.cloudcanards.assets.Assets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.IOException;
import java.util.Properties;

/**
 * VersionLabel
 *
 * @author creativitRy
 */
public class VersionLabel extends Table
{
	public VersionLabel(int alignment)
	{
		super(CloudCanards.getInstance().getSkin());
		setFillParent(true);
		pad(25f);
		align(alignment);
		
		add(new Label("CloudCanards-" + getProperties(), getSkin(), "small", Color.WHITE));
	}
	
	public String getProperties()
	{
		Properties properties = new Properties();
		try
		{
			properties.load(Gdx.files.internal(Assets.DIR + "version.properties").read());
			
			return properties.getProperty("artifactVersion") + "-" + properties.getProperty("artifactDate");
		}
		catch (IOException | IllegalArgumentException | GdxRuntimeException e)
		{
			return "???";
		}
	}
}
