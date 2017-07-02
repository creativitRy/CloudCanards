package com.cloudcanards.ui;

import com.cloudcanards.CloudCanards;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * FpsLabel
 *
 * @author creativitRy
 */
public class FpsCounter extends Table
{
	private Label label;
	
	/**
	 * Constructs a new fps counter
	 *
	 * @param alignment one of the {@link com.badlogic.gdx.utils.Align Align} constants
	 */
	public FpsCounter(int alignment)
	{
		super(CloudCanards.getInstance().getSkin());
		setFillParent(true);
		pad(25f);
		align(alignment);
		
		label = new Label("60", getSkin(), "small", Color.WHITE);
		add(label);
	}
	
	@Override
	public void act(float delta)
	{
		super.act(delta);
		label.setText(Integer.toString(Gdx.graphics.getFramesPerSecond()));
	}
}
