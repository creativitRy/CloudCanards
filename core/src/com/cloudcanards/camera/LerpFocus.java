package com.cloudcanards.camera;

import com.badlogic.gdx.math.MathUtils;
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
	
	private float prevZoom;
	
	public LerpFocus(CameraFocus focus, float lerpStrength)
	{
		this.focus = focus;
		prev = null;
		strength = lerpStrength;
		prevZoom = focus.getScale();
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
	
	@Override
	public float getScale()
	{
		float scale = focus.getScale();
		if (scale == prevZoom)
		{
			return scale;
		}
		
		if (scale < prevZoom)
		{
			return prevZoom = MathUtils.lerp(prevZoom, scale, strength / 8f);
		}
		
		return prevZoom = MathUtils.lerp(prevZoom, scale, strength / 2f);
	}
}
