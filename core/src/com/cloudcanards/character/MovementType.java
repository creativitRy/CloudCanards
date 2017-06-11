package com.cloudcanards.character;

/**
 * MovementSpeed
 *
 * @author creativitRy
 */
public enum MovementType
{
	WALK(5),
	RUN(12),
	SPRINT(20);
	
	private float defaultSpeed;
	
	MovementType(float defaultSpeed)
	{
		this.defaultSpeed = defaultSpeed;
	}
	
	public float getDefaultSpeed()
	{
		return defaultSpeed;
	}
}
