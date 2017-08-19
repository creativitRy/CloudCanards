package com.cloudcanards.ui;

import com.cloudcanards.CloudCanards;
import com.cloudcanards.character.AbstractCharacter;
import com.cloudcanards.screens.GameScreen;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.utils.Align;

/**
 * FloatingHealthBar
 *
 * @author creativitRy
 */
public class FloatingHealthBar extends ProgressBar
{
	private final AbstractCharacter character;
	private Vector2 pos;
	
	public FloatingHealthBar(AbstractCharacter character)
	{
		super(0, character.getMaxHealth(), 0.5f, false, CloudCanards.getInstance().getSkin());
		setSize(50, 1);
		getStyle().background.setMinHeight(5);
		getStyle().knobBefore.setMinHeight(5);
		this.character = character;
		pos = new Vector2();
	}
	
	@Override
	public void act(float delta)
	{
		super.act(delta);
		setValue(character.getHealth());
		
		pos.set(GameScreen.worldToScreenCoords(character.getBody().getPosition().x, character.getBody().getPosition().y + 2f));
		
		setPosition(pos.x, pos.y + 2f, Align.center);
	}
}
