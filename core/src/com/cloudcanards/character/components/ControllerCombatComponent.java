package com.cloudcanards.character.components;

import com.cloudcanards.behavior.Updateable;
import com.cloudcanards.character.AbstractCharacter;
import com.cloudcanards.health.combat.AttackType;

/**
 * ControllerCombatComponent
 *
 * @author creativitRy
 */
public class ControllerCombatComponent extends AbstractCombatControllerComponent implements Updateable
{
	private float x;
	private float y;
	private boolean attack;
	
	public ControllerCombatComponent(AbstractCharacter character, WeaponComponent weaponComponent)
	{
		super(character, weaponComponent);
	}
	
	public void onAxisMoved(boolean vertical, float value)
	{
		if (vertical)
		{
			y = value;
		}
		else
		{
			x = value;
		}
	}
	
	public void onAttackInput(boolean down)
	{
		attack = down;
	}
	
	@Override
	public void update(float delta)
	{
		if (attack)
		{
			if (y < -0.2f)
			{
				attack(AttackType.RAM);
			}
			else if (x < -0.2f || x > 0.2f)
			{
				attack(AttackType.STAB);
			}
			else
			{
				attack(AttackType.PART);
			}
		}
	}
}
