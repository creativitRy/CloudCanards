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
public class MainHUD
{
	private Table table;
	
	public MainHUD()
	{
		table = new Table(CloudCanards.getInstance().getSkin());
		table.setFillParent(true);
		
		table.pad(50f);
		
		table.add(new Label("Test text", CloudCanards.getInstance().getSkin(), "default", Color.WHITE));
		//table.setVisible();
		table.top().left();
		table.row();
		ProgressBar progressBar = new ProgressBar(0, 5, 0.5f, false, CloudCanards.getInstance().getSkin());
		table.add(progressBar);
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
	
	public Table getTable()
	{
		return table;
	}
}
