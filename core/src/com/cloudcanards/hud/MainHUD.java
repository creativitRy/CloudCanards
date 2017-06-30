package com.cloudcanards.hud;

import com.cloudcanards.CloudCanards;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * MainHUD
 *
 * @author creativitRy
 */
public class MainHUD extends Table
{
	public MainHUD()
	{
		super(CloudCanards.getInstance().getSkin());
	}
}
