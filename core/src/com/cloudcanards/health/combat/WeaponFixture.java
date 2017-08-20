package com.cloudcanards.health.combat;

import com.cloudcanards.box2d.AdvancedCollidable;
import com.cloudcanards.box2d.CollisionFilters;
import com.cloudcanards.character.AbstractCharacter;
import org.jetbrains.annotations.Nullable;

import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * WeaponFixture
 *
 * @author creativitRy
 */
public class WeaponFixture implements AdvancedCollidable
{
	private AbstractWeapon weapon;
	
	public WeaponFixture(AbstractWeapon weapon)
	{
		this.weapon = weapon;
	}
	
	@Override
	public void onBeginContact(Fixture contactedFixture)
	{
		if (contactedFixture.getFilterData().categoryBits == CollisionFilters.WEAPON)
		{
			AbstractWeapon opponentWeapon = ((WeaponFixture) contactedFixture.getUserData()).weapon;
			weapon.notifyCollision(opponentWeapon.getCharacter());
		}
		else if (contactedFixture.getFilterData().categoryBits == CollisionFilters.CHARACTER)
		{
			@Nullable
			AbstractCharacter character = (AbstractCharacter) contactedFixture.getBody().getUserData();
			
			if (character == null)
			{
				return;
			}
			
			weapon.notifyCollision(character);
		}
	}
	
	@Override
	public void onEndContact(Fixture contactedFixture)
	{
	
	}
}
