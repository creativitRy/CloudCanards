package com.cloudcanards.character;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector2;

/**
 * CharacterState
 *
 * @author creativitRy
 */
public enum CharacterState implements State<AbstractCharacter>
{
	IDLE,
	RUN,
	JUMP
		{
			@Override
			public void enter(AbstractCharacter entity)
			{
				entity.getBody().setLinearDamping(0f);
				entity.getBody().setGravityScale(1f);
				
				entity.getBody().setLinearVelocity(entity.getBody().getLinearVelocity().x, 0);
				Vector2 pos = entity.getBody().getWorldCenter();
				//body.setLinearVelocity(body.getLinearVelocity().x, 15);
				entity.getBody().applyLinearImpulse(0, JUMP_VELOCITY * entity.getBody().getMass(), pos.x, pos.y, true);
			}
			
			@Override
			public void update(AbstractCharacter entity)
			{
				if (entity.getBody().getLinearVelocity().y <= 0)
				{
					entity.getStateMachine().changeState(CharacterState.FALL);
				}
			}
		},
	FALL
		{
			@Override
			public void enter(AbstractCharacter entity)
			{
			
			}
			
			@Override
			public void update(AbstractCharacter entity)
			{
				if (entity.getBody().getLinearVelocity().y <= 0)
				{
					if (entity.getBody().getGravityScale() != 2f)
					{
						entity.getBody().setGravityScale(2f);
					}
				}
			}
		},
	GRAPPLE
		{
			@Override
			public void enter(AbstractCharacter entity)
			{
				entity.getBody().setGravityScale(1f);
			}
		},;
	
	private static final int JUMP_VELOCITY = 17;
	
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
