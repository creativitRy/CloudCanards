package com.cloudcanards.character.components;

import com.cloudcanards.character.AbstractCharacter;
import com.cloudcanards.health.combat.AttackType;
import com.cloudcanards.input.InputAction;
import com.cloudcanards.input.InputListener;
import com.cloudcanards.input.InputManager;
import com.cloudcanards.input.InputType;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * MouseCombatComponent
 *
 * @author creativitRy
 */
public class MouseCombatComponent extends AbstractCombatControllerComponent implements InputListener
{
	public MouseCombatComponent(AbstractCharacter character, WeaponComponent weaponComponent, boolean enableInput)
	{
		super(character, weaponComponent);
		mouseEnabled = enableInput;
		InputManager.register(this);
	}
	
	@Override
	public boolean onInput(InputAction action, InputType type, Object... args)
	{
		if (action == InputAction.ATTACK)
		{
			if (args[0] instanceof Boolean && (Boolean) args[0])
			{
				if (Gdx.input.isKeyPressed(Input.Keys.W))
				{
					attack(AttackType.RAM);
				}
				else if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.D))
				{
					attack(AttackType.STAB);
				}
				else
				{
					attack(AttackType.PART);
				}
			}
		}
		return false;
	}
	
	private boolean mouseEnabled;
	
	@Override
	public boolean isEnabled()
	{
		return mouseEnabled;
	}
	
	@Override
	public void enable()
	{
		mouseEnabled = true;
	}
	
	@Override
	public void disable()
	{
		mouseEnabled = false;
	}
}
