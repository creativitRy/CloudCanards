package com.cloudcanards.ui;

import com.cloudcanards.CloudCanards;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Timer;

/**
 * MainHUD
 *
 * @author creativitRy
 */
public class MainHUD extends Table
{
	private Table table;
	
	public MainHUD()
	{
		super(CloudCanards.getInstance().getSkin());
		setFillParent(true);
		
		pad(50f);
		
		add(new Label("Test text", CloudCanards.getInstance().getSkin(), "default", Color.WHITE));
		//setVisible();
		top().left();
		row();
		ProgressBar progressBar = new ProgressBar(0, 5, 0.5f, false, CloudCanards.getInstance().getSkin());
		add(progressBar);
		progressBar.setValue(3f);
		progressBar.setAnimateDuration(0.1f);
		Timer.schedule(new Timer.Task()
		{
			@Override
			public void run()
			{
				progressBar.setValue(0.5f);
			}
		}, 2f);
	}
}
