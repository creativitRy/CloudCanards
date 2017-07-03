package com.cloudcanards.ui;

import com.cloudcanards.CloudCanards;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * AbstractValueLabel
 *
 * @author creativitRy
 */
public abstract class AbstractValueLabel extends Table
{
	private Label label;
	
	public AbstractValueLabel(int alignment)
	{
		super(CloudCanards.getInstance().getSkin());
		setFillParent(true);
		pad(25f);
		align(alignment);
		
		label = new Label("", getSkin(), "small", Color.WHITE);
		add(label);
	}
	
	@Override
	public void act(float delta)
	{
		super.act(delta);
		label.setText(updateText());
	}
	
	/**
	 * This sets the text
	 *
	 * @return text to display
	 */
	public abstract String updateText();
}
