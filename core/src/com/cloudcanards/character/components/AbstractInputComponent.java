package com.cloudcanards.character.components;

import com.cloudcanards.character.AbstractCharacter;

/**
 * AbstractInputComponent
 *
 * @author creativitRy
 */
public abstract class AbstractInputComponent extends AbstractComponent
{
	private boolean walk;
	private boolean sprint;
	private boolean left;
	private boolean right;
	
	public AbstractInputComponent(AbstractCharacter character)
	{
		super(character);
	}
	
	protected void setMovement()
	{
		if (left == right)
		{
			character.setMovementDir(0);
		}
		else if (left)
		{
			character.setMovementDir(-1);
		}
		else
		{
			character.setMovementDir(1);
		}
	}
	
	protected void setSpeed()
	{
		if (walk == sprint)
		{
			character.run();
		}
		else if (walk)
		{
			character.walk();
		}
		else
		{
			character.sprint();
		}
	}
	
	protected boolean isWalk()
	{
		return walk;
	}
	
	protected void setWalk(boolean walk)
	{
		this.walk = walk;
	}
	
	protected boolean isSprint()
	{
		return sprint;
	}
	
	protected void setSprint(boolean sprint)
	{
		this.sprint = sprint;
	}
	
	protected boolean isLeft()
	{
		return left;
	}
	
	protected void setLeft(boolean left)
	{
		this.left = left;
	}
	
	protected boolean isRight()
	{
		return right;
	}
	
	protected void setRight(boolean right)
	{
		this.right = right;
	}
}
