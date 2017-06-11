package com.cloudcanards.components;

import com.cloudcanards.camera.CameraFocus;
import com.cloudcanards.character.AbstractCharacter;

import com.badlogic.gdx.math.Vector2;

/**
 * CameraFocusComponent
 *
 * @author creativitRy
 */
public class CameraFocusComponent extends AbstractComponent implements CameraFocus
{
	public CameraFocusComponent(AbstractCharacter character)
	{
		super(character);
	}
	
	@Override
	public Vector2 getPosition()
	{
		return character.getBody().getPosition();
	}
}
