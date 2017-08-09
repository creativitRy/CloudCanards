package com.cloudcanards.health.combat;

import org.jetbrains.annotations.Nullable;

/**
 * AttackTypes
 *
 * @author GahwonLee
 */
public enum AttackType
{
	STAB(false, false, true),
	RAM(true, false, false),
	PART(false, true, false);
	
	public static final int NUM_TYPES = 3;
	/**
	 * true = can beat the type at that index ordinal
	 * false = cannot beat
	 */
	private boolean[] canBeat;
	
	
	AttackType(boolean stab, boolean ram, boolean part)
	{
		canBeat = new boolean[NUM_TYPES];
		
		canBeat[0] = stab;
		canBeat[1] = ram;
		canBeat[2] = part;
	}
	
	/**
	 * Checks whether this can beat the other attack type
	 *
	 * @param attacker attack type that is attacking this. Can be null if not attacking
	 * @return true if this can beat the attacker (or if attacker is null), false otherwise
	 */
	public boolean canBeat(@Nullable AttackType attacker)
	{
		if (attacker == null)
			return true;
		return canBeat[attacker.ordinal()];
	}
}
