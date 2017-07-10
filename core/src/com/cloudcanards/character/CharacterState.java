package com.cloudcanards.character;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

/**
 * CharacterState
 *
 * @author creativitRy
 */
public enum CharacterState implements State<AbstractCharacter>
{
	IDLE,
	RUN,
	JUMP,
	FALL,
	GRAPPLE,;
	
	
	@Override
	public void enter(AbstractCharacter entity)
	{
	
	}
	
	@Override
	public void update(AbstractCharacter entity)
	{
	
	}
	
	@Override
	public void exit(AbstractCharacter entity)
	{
	
	}
	
	@Override
	public boolean onMessage(AbstractCharacter entity, Telegram telegram)
	{
		return false;
	}
}
