package com.cloudcanards.camera;

import org.jetbrains.annotations.NotNull;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * MultiCameraFocus
 *
 * @author creativitRy
 */
public class MultiCameraFocus implements CameraFocus
{
	private static final float PADDING = 4f / 7f; // this * ~7 tiles are padded on the edges
	
	@NotNull
	private final Array<CameraFocus> foci;
	private final Vector2 position;
	private float zoom;
	
	private final float width;
	private final float height;
	
	private final float minX;
	private final float minY;
	private final float maxX;
	private final float maxY;
	
	public MultiCameraFocus(float width, float height, float minX, float minY, float maxX, float maxY)
	{
		foci = new Array<>();
		position = new Vector2();
		
		this.width = width;
		this.height = height;
		this.minX = minX;
		this.minY = minY;
		this.maxX = maxX;
		this.maxY = maxY;
		
	}
	
	@Override
	public Vector2 getPosition()
	{
		if (foci.size == 0)
		{
			return position;
		}
		
		float xMin = maxX;
		float yMin = maxY;
		float xMax = minX;
		float yMax = minY;
		
		position.set(0, 0);
		for (CameraFocus focus : foci)
		{
			Vector2 pos = focus.getPosition();
			position.add(pos);
			
			xMin = Math.min(pos.x, xMin);
			yMin = Math.min(pos.y, yMin);
			xMax = Math.max(pos.x, xMax);
			yMax = Math.max(pos.y, yMax);
		}
		
		//don't go out of bounds
		xMin = Math.max(minX, xMin);
		yMin = Math.max(minY, yMin);
		xMax = Math.min(maxX, xMax);
		yMax = Math.min(maxY, yMax);
		
		zoom = (xMax - xMin + PADDING) / width;
		float temp = (yMax - yMin + PADDING) / height;
		
		if (temp > zoom)
		{
			zoom = temp;
		}
		
		return position.scl(1f / foci.size);
	}
	
	@Override
	public float getScale()
	{
		if (foci.size < 2)
		{
			return 0.75f;
		}
		return Math.min(16f, Math.max(0.75f, zoom));
	}
	
	@NotNull
	public Array<CameraFocus> getFoci()
	{
		return foci;
	}
}
