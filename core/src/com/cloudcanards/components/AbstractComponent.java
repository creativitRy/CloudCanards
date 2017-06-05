package com.cloudcanards.components;

import com.cloudcanards.behavior.Updateable;
import com.cloudcanards.character.AbstractCharacter;

/**
 * Movement components that the character uses as functionality
 *
 * @author creativitRy
 */
public abstract class AbstractComponent implements Updateable
{
	private AbstractCharacter character;
	
	public AbstractComponent(AbstractCharacter character)
	{
		this.character = character;
	}
}
