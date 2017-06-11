package com.cloudcanards.camera;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Vector2;

/**
 * CameraFocus
 *
 * @author creativitRy
 */
public interface CameraFocus
{
	Vector2 getPosition();
	
	default void setPosition(Camera camera)
	{
		Vector2 pos = getPosition();
		camera.position.x = pos.x;
		camera.position.y = pos.y;
	}
}
