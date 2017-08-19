package com.cloudcanards.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;

/**
 * CameraFocus
 *
 * @author creativitRy
 */
public interface CameraFocus
{
	Vector2 getPosition();
	
	default void setPosition(OrthographicCamera camera)
	{
		Vector2 pos = getPosition();
		camera.position.x = pos.x;
		camera.position.y = pos.y;
	}
	
	default float getScale()
	{
		return 1f;
	}
	
	default void setScale(OrthographicCamera camera)
	{
		camera.zoom = getScale();
	}
}
