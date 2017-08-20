package com.cloudcanards.health;

import com.cloudcanards.health.combat.AttackType;
import org.jetbrains.annotations.Nullable;

/**
 * Anything that has health and can be damaged
 *
 * @author creativitRy
 */
public interface Damageable
{
	int getHealth();
	
	void setHealth(int health);
	
	/**
	 * Gets the maximum health possible. Default is an unchangeable 5.
	 *
	 * @return maximum health
	 */
	default int getMaxHealth()
	{
		return 5;
	}
	
	/**
	 * Sets the maximum health possible. Default impl does nothing
	 *
	 * @param maxHealth new maximum health
	 */
	default void setMaxHealth(int maxHealth)
	{
	
	}
	
	/**
	 * Checks whether health <= 0
	 *
	 * @return true if health is empty
	 */
	default boolean isDead()
	{
		return getHealth() <= 0;
	}
	
	/**
	 * Checks whether health > maxHealth
	 *
	 * @return true if health is over maxHealth
	 */
	default boolean isOverMaxHealth()
	{
		return getHealth() > getMaxHealth();
	}
	
	/**
	 * Call when {@link #isDead()} is true
	 */
	void onDeath();
	
	/**
	 * Call when {@link #isOverMaxHealth()} is true.
	 * <p>
	 * Default behavior sets the health back to max health
	 */
	default void onOverMax()
	{
		setHealth(getMaxHealth());
	}
	
	/**
	 * Called when {@link #damage(int)} is called
	 *
	 * @return if false is returned. Damage is not dealt
	 */
	default boolean canDamage()
	{
		return true;
	}
	
	/**
	 * Decrease the health by amount.
	 * <p>
	 * Checks {@link #isDead()} and calls {@link #onDeath()} if true
	 *
	 * @param amount integer to decrease health by
	 * @return true if damage is dealt
	 */
	default boolean damage(int amount)
	{
		if (!canDamage())
		{
			return false;
		}
		
		setHealth(getHealth() - amount);
		
		if (isDead())
		{
			onDeath();
		}
		
		onDamage(amount);
		return true;
	}
	
	/**
	 * Called after damage is done and death is checked
	 *
	 * @param amount amount of damage dealt
	 */
	default void onDamage(int amount)
	{
	
	}
	
	/**
	 * Increase the health by amount.
	 * <p>
	 * Checks {@link #isOverMaxHealth()} and calls {@link #onOverMax()} if true
	 *
	 * @param amount integer to increase health by
	 */
	default void heal(int amount)
	{
		setHealth(getHealth() + amount);
		
		if (isOverMaxHealth())
		{
			onOverMax();
		}
	}
	
	/**
	 * Sets the health to max health
	 */
	default void resetHealth()
	{
		setHealth(getMaxHealth());
	}
	
	/**
	 * Gets the current attack type that this is executing
	 *
	 * @return current attack type or null if not attacking
	 */
	@Nullable
	AttackType getCurrentAttackType();
}
