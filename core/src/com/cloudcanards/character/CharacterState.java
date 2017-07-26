package com.cloudcanards.character;

import com.cloudcanards.time.TimeManager;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/**
 * CharacterState
 *
 * @author creativitRy
 */
public enum CharacterState implements State<AbstractCharacter>
{
	IDLE
		{
			@Override
			public void enter(AbstractCharacter entity)
			{
				update(entity);
			}
			
			@Override
			public void update(AbstractCharacter entity)
			{
				if (!groundCheck(entity))
				{
					if (entity.getMovementDir() != 0)
					{
						entity.getStateMachine().changeState(RUN);
					}
				}
				
				horizMove(entity);
				
			}
		},
	RUN
		{
			@Override
			public void update(AbstractCharacter entity)
			{
				if (!groundCheck(entity))
				{
					if (MathUtils.isEqual(entity.getBody().getLinearVelocity().x, entity.getPlatformVelocity().x, 0.01f))
					{
						entity.getStateMachine().changeState(IDLE);
					}
				}
				
				horizMove(entity);
			}
		},
	JUMP
		{
			@Override
			public void enter(AbstractCharacter entity)
			{
				entity.getBody().setLinearDamping(0f);
				entity.getBody().setGravityScale(1f);
				
				entity.getBody().setLinearVelocity(entity.getBody().getLinearVelocity().x, 0);
				Vector2 pos = entity.getBody().getWorldCenter();
				entity.getBody().applyLinearImpulse(0, JUMP_VELOCITY * entity.getBody().getMass(), pos.x, pos.y, true);
				
				entity.getJumpContact().jump();
			}
			
			@Override
			public void update(AbstractCharacter entity)
			{
				if (entity.isStopJumping())
				{
					if (!entity.isGrounded())
					{
						entity.getBody().setGravityScale(4f);
						entity.getStateMachine().changeState(FALL);
						return;
					}
				}
				if (entity.getBody().getLinearVelocity().y <= 0)
				{
					entity.getStateMachine().changeState(FALL);
				}
				
				horizMove(entity);
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
				if (entity.isGrounded())
				{
					entity.getStateMachine().changeState(IDLE);
				}
				if (entity.getBody().getLinearVelocity().y <= 0)
				{
					if (entity.getBody().getGravityScale() != 2f)
					{
						entity.getBody().setGravityScale(2f);
					}
				}
				
				horizMove(entity);
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
	
	/**
	 * Checks whether the entity is grounded and if false, changes to fall state
	 *
	 * @return true if state was altered
	 */
	private static boolean groundCheck(AbstractCharacter entity)
	{
		if (!entity.isGrounded())
		{
			if (entity.getStateMachine().getCurrentState() != CharacterState.JUMP)
			{
				entity.getStateMachine().changeState(CharacterState.FALL);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * When the entity wants to move left or right
	 */
	private static void horizMove(AbstractCharacter entity)
	{
		Vector2 vel = entity.getBody().getLinearVelocity();
		float slowness = 0.25f;
		if (!entity.isGrounded())
		{
			slowness = 0.75f;
		}
		
		if (entity.getMovementDir() != 0)
		{
			if ((vel.x >= -entity.getMovementSpeed() && entity.getMovementDir() < 0) || (vel.x <= entity.getMovementSpeed() && entity.getMovementDir() > 0))
			{
				entity.setHorizontalVel(entity.getMovementDir() * entity.getMovementSpeed(), TimeManager.getInstance().getDelta(), slowness);
			}
		}
		else
		{
			entity.setHorizontalVel(0, TimeManager.getInstance().getDelta(), slowness);
		}
	}
	
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
