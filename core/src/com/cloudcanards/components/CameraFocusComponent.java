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
		//todo: define how much offset from the previous resting position the player needs to move in order to
		// trigger the camera to move. Once out of that box (offset), the camera should lerp to the center of the box
		// and the box should center on the player
		return character.getBody().getPosition();
	}
}
