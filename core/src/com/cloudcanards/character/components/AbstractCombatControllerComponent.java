package com.cloudcanards.character.components;

import com.cloudcanards.character.AbstractCharacter;
import com.cloudcanards.health.combat.AttackType;

/**
 * AbstractWeaponControllerComponent
 *
 * @author creativitRy
 */
public abstract class AbstractCombatControllerComponent extends AbstractComponent
{
	
	private final WeaponComponent weaponComponent;
	
	public AbstractCombatControllerComponent(AbstractCharacter character, WeaponComponent weaponComponent)
	{
		super(character);
		this.weaponComponent = weaponComponent;
	}
	
	protected void attack(AttackType attackType)
	{
		weaponComponent.attack(attackType);
	}
}
