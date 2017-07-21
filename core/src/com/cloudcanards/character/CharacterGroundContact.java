package com.cloudcanards.character;

import com.cloudcanards.box2d.AdvancedCollidable;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * CharacterGroundContact
 *
 * @author creativitRy
 */
public class CharacterGroundContact implements AdvancedCollidable
{
	private AbstractCharacter character;
	private float halfHeight;
	
	private float numContacts;
	
	public CharacterGroundContact(AbstractCharacter character, float halfHeight)
	{
		this.character = character;
		this.halfHeight = halfHeight;
		
		numContacts = 0;
	}
	
	@Override
	public void onBeginContact(Fixture contactedFixture)
	{
		/*if (!isBeneathCharacter(contactedFixture))
			return;*/
		numContacts++;
		character.setGroundFriction(contactedFixture.getFriction());
	}
	
	@Override
	public void onEndContact(Fixture contactedFixture)
	{
		/*if (!isBeneathCharacter(contactedFixture))
			return;*/
		numContacts--;
		if (numContacts == 0)
		{
			character.setGroundFriction(-1f);
		}
	}
	
	/**
	 * Checks if fixture is below character
	 * todo: change to sensor
	 */
	private boolean temp;
	
	private boolean isBeneathCharacter(Fixture contactedFixture)
	{
		Vector2 pos = character.getBody().getPosition();
		if (contactedFixture.testPoint(pos.x, pos.y -
			halfHeight))
			return true;
		temp = false;
		contactedFixture.getBody().getWorld().rayCast((fixture, point, normal, fraction) ->
		{
			if (fixture != contactedFixture)
				return 1;
			temp = true;
			return 0;
		}, pos.x, pos.y, pos.x, pos.y - halfHeight * 2);
		
		return temp;
	}
}
