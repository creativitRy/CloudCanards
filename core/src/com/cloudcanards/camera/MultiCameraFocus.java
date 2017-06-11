package com.cloudcanards.camera;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * MultiCameraFocus
 *
 * @author creativitRy
 */
public class MultiCameraFocus implements CameraFocus
{
	private Array<CameraFocus> foci;
	
	public MultiCameraFocus()
	{
		foci = new Array<>();
	}
	
	public Array<CameraFocus> getFoci()
	{
		return foci;
	}
	
	@Override
	public Vector2 getPosition()
	{
		Vector2 position = new Vector2();
		for (CameraFocus focus : foci)
		{
			position.add(focus.getPosition());
		}
		
		return position.scl(1f / foci.size);
	}
}
