package com.cloudcanards.health.combat;

import com.cloudcanards.health.Damageable;

/**
 * A character that can attack {@link Damageable damageables}
 *
 * @author GahwonLee
 * @see #attack(Damageable, AttackType)
 */
public interface Attackable
{
	void onAttackSuccessful(Damageable damageable, AttackType attackType);
	
	void onAttackFailed(Damageable damageable, AttackType attackType);
	
	/**
	 * Attacks a {@link Damageable} and sees what happens.
	 * Calls either {@link #onAttackSuccessful(Damageable, AttackType)} or {@link #onAttackFailed(Damageable,
	 * AttackType)}
	 *
	 * @param damageable damageable to attack
	 * @param attackType attack type to attack damageable with
	 */
	default void attack(Damageable damageable, AttackType attackType)
	{
		if (attackType.canBeat(damageable.getCurrentAttackType()))
		{
			onAttackSuccessful(damageable, attackType);
		}
		else
		{
			onAttackFailed(damageable, attackType);
		}
	}
}
