package com.cloudcanards.components;

import com.cloudcanards.character.AbstractCharacter;

import com.badlogic.gdx.math.Vector2;

/**
 * GrappleComponent
 *
 * @author creativitRy
 */
public class GrappleComponent extends AbstractComponent
{
	public GrappleComponent(AbstractCharacter character)
	{
		super(character);
	}
	
	public Vector2 getPosition()
	{
		return character.getBody().getPosition();
	}
}
