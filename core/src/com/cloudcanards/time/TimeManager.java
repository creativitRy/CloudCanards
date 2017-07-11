package com.cloudcanards.time;


/**
 * TimeManager
 *
 * @author creativitRy
 */
public class TimeManager
{
	private static final TimeManager INSTANCE = new TimeManager();
	
	public static TimeManager getInstance()
	{
		return INSTANCE;
	}
	
	private static final float MAX_DELTA = 1f / 20f; //20 fps or below slows down the game instead of skipping frames
	
	private float delta;
	private float timeMultiplier;
	
	private TimeManager()
	{
		timeMultiplier = 1;
	}
	
	/**
	 * Regular delta time
	 *
	 * @return
	 */
	public float getDelta()
	{
		if (timeMultiplier == 1)
		{
			return getUnmodifiedDelta();
		}
		return getUnmodifiedDelta() * timeMultiplier;
	}
	
	/**
	 * Used for purposes that aren't bound by time speeding mechanics
	 */
	public float getUnmodifiedDelta()
	{
		return Math.min(delta, MAX_DELTA);
	}
	
	public void setDelta(float delta)
	{
		this.delta = delta;
	}
	
	/**
	 * 1 = time is normal. <1 = time is slower. 0 = time is stopped
	 */
	public float getTimeMultiplier()
	{
		return timeMultiplier;
	}
	
	/**
	 * 1 = time is normal. <1 = time is slower. 0 = time is stopped. WARNING: Only use within CloudCanards
	 */
	public void setTimeMultiplier(float timeMultiplier)
	{
		this.timeMultiplier = timeMultiplier;
	}
}
