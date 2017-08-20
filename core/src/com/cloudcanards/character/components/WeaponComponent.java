package com.cloudcanards.character.components;

import com.cloudcanards.behavior.Updateable;
import com.cloudcanards.character.AbstractCharacter;
import com.cloudcanards.graphics.Renderable;
import com.cloudcanards.health.combat.AbstractWeapon;
import com.cloudcanards.health.combat.AttackType;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

/**
 * WeaponComponent
 *
 * @author creativitRy
 */
public class WeaponComponent extends AbstractComponent implements Updateable, Renderable
{
	private AbstractWeapon weapon;
	
	public WeaponComponent(AbstractCharacter character, World world)
	{
		super(character);
		
		weapon = new AbstractWeapon(character, world, 3.5f, 0.2f, 0.2f, 4f) {};
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
	
	@Override
	public void render(SpriteBatch batch, float delta)
	{
		weapon.render(batch, delta);
	}
}
