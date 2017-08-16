package com.cloudcanards.health.combat;

/**
 * To prevent friendly fire
 *
 * @author creativitRy
 * @see #canDamage(CombatTeam)
 */
public enum CombatTeam
{
	/**
	 * Yanna's team, can damage enemies
	 */
	ALLY(false, true, true),
	/**
	 * Enemy's team, can damage allies
	 */
	ENEMY(true, false, true),
	/**
	 * Can damage both allies and enemies, can be damage by both
	 */
	ETC(true, true, true);
	
	private static final int NUM_TEAMS = 3;
	private boolean[] canDamage;
	
	CombatTeam(boolean damageAlly, boolean damageEnemy, boolean damageEtc)
	{
		canDamage = new boolean[NUM_TEAMS];
		
		canDamage[0] = damageAlly;
		canDamage[1] = damageEnemy;
		canDamage[2] = damageEtc;
	}
	
	/**
	 * Checks whether this can damage team
	 *
	 * @param team what this is trying to damage
	 * @return true if this can damage team
	 */
	public boolean canDamage(CombatTeam team)
	{
		return canDamage[team.ordinal()];
	}
}
