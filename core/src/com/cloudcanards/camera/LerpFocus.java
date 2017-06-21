package com.cloudcanards.camera;

import com.badlogic.gdx.math.Vector2;

/**
 * LerpFocus
 *
 * @author creativitRy
 */
public class LerpFocus implements CameraFocus
{
	private CameraFocus focus;
	private Vector2 prev;
	private float strength;
	
	public LerpFocus(CameraFocus focus, float lerpStrength)
	{
		this.focus = focus;
		prev = null;
		strength = lerpStrength;
	}
	
	@Override
	public Vector2 getPosition()
	{
		if (prev == null)
		{
			prev = new Vector2(focus.getPosition());
			return prev;
		}
		Vector2 pos = focus.getPosition();
		return prev.lerp(pos, strength);
	}
}
