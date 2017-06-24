package com.cloudcanards.components;

import com.cloudcanards.character.AbstractCharacter;
import com.cloudcanards.graphics.Renderable;

/**
 * AbstractRenderableComponent
 *
 * @author creativitRy
 */
public abstract class AbstractRenderableComponent extends AbstractComponent implements Renderable
{
	public AbstractRenderableComponent(AbstractCharacter character)
	{
		super(character);
	}
}
