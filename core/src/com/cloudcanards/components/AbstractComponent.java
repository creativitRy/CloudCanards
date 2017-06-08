package com.cloudcanards.components;

import com.cloudcanards.character.AbstractCharacter;

/**
 * Movement components that the character uses as functionality
 *
 * @author creativitRy
 */
public abstract class AbstractComponent
{
	protected AbstractCharacter character;
	
	public AbstractComponent(AbstractCharacter character)
	{
		this.character = character;
	}
}
