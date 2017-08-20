package com.cloudcanards.character.components;

import com.cloudcanards.behavior.Updateable;
import com.cloudcanards.character.AbstractCharacter;
import com.cloudcanards.health.combat.AbstractWeapon;
import com.cloudcanards.health.combat.AttackType;

import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;

/**
 * WeaponComponent
 *
 * @author creativitRy
 */
public class WeaponComponent extends AbstractComponent implements Updateable
{
	private AbstractWeapon weapon;
	
	public WeaponComponent(AbstractCharacter character, World world)
	{
		super(character);
		
		weapon = new AbstractWeapon(character, world, 3.5f, 0.2f, 0.2f, 4f) {};
		
		Timer.schedule(new Timer.Task()
		{
			@Override
			public void run()
			{
				attack(AttackType.RAM);
			}
		}, 3f);
	}
	
	public void attack(AttackType attackType)
	{
		weapon.startAttack(attackType);
	}
	
	@Override
	public void update(float delta)
	{
		weapon.update(delta);
	}
}
