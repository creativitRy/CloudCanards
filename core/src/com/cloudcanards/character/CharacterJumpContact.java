package com.cloudcanards.character;

import com.cloudcanards.behavior.Updateable;
import com.cloudcanards.box2d.AdvancedCollidable;

import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * CharacterJumpContact
 *
 * @author creativitRy
 */
public class CharacterJumpContact implements AdvancedCollidable, Updateable
{
	private static final float COOLDOWN_TIME = 0.25f;
	
	private AbstractCharacter character;
	private float timeSinceGrounded;
	private float cooldown;
	private boolean canJump;
	
	private float numContacts;
	
	public CharacterJumpContact(AbstractCharacter character)
	{
		this.character = character;
		cooldown = 0;
		
		numContacts = 0;
	}
	
	public boolean canJump()
	{
		return numContacts > 0 && cooldown <= 0;
	}
	
	void jump()
	{
		cooldown = COOLDOWN_TIME;
	}
	
	void resetTimeSinceGrounded()
	{
		timeSinceGrounded = 0;
	}
	
	@Override
	public void onBeginContact(Fixture contactedFixture)
	{
		numContacts++;
	}
	
	@Override
	public void onEndContact(Fixture contactedFixture)
	{
		numContacts--;
	}
	
	@Override
	public void update(float delta)
	{
		if (cooldown > 0)
		{
			cooldown -= delta;
		}
		
	}
}
