package com.cloudcanards.input;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

/**
 * ControllerInputListener
 *
 * @author creativitRy
 */
public interface ControllerInputListener
{
	/**
	 * A {@link Controller} got disconnected.
	 */
	void disconnected();
	
	/**
	 * A button on the {@link Controller} was pressed. The buttonCode is controller specific. The
	 * <code>com.badlogic.gdx.controllers.mapping</code> package hosts button constants for known controllers.
	 *
	 * @param buttonCode
	 * @return whether to hand the event to other listeners.
	 */
	boolean buttonDown(int buttonCode);
	
	/**
	 * A button on the {@link Controller} was released. The buttonCode is controller specific. The
	 * <code>com.badlogic.gdx.controllers.mapping</code> package hosts button constants for known controllers.
	 *
	 * @param buttonCode
	 * @return whether to hand the event to other listeners.
	 */
	boolean buttonUp(int buttonCode);
	
	/**
	 * An axis on the {@link Controller} moved. The axisCode is controller specific. The axis value is in the range [-1,
	 * 1]. The
	 * <code>com.badlogic.gdx.controllers.mapping</code> package hosts axes constants for known controllers.
	 *
	 * @param axisCode
	 * @param value    the axis value, -1 to 1
	 * @return whether to hand the event to other listeners.
	 */
	boolean axisMoved(int axisCode, float value);
	
	/**
	 * A POV on the {@link Controller} moved. The povCode is controller specific. The
	 * <code>com.badlogic.gdx.controllers.mapping</code> package hosts POV constants for known controllers.
	 *
	 * @param povCode
	 * @param value
	 * @return whether to hand the event to other listeners.
	 */
	boolean povMoved(int povCode, PovDirection value);
	
	/**
	 * An x-slider on the {@link Controller} moved. The sliderCode is controller specific. The
	 * <code>com.badlogic.gdx.controllers.mapping</code> package hosts slider constants for known controllers.
	 *
	 * @param sliderCode
	 * @param value
	 * @return whether to hand the event to other listeners.
	 */
	boolean xSliderMoved(int sliderCode, boolean value);
	
	/**
	 * An y-slider on the {@link Controller} moved. The sliderCode is controller specific. The
	 * <code>com.badlogic.gdx.controllers.mapping</code> package hosts slider constants for known controllers.
	 *
	 * @param sliderCode
	 * @param value
	 * @return whether to hand the event to other listeners.
	 */
	boolean ySliderMoved(int sliderCode, boolean value);
	
	/**
	 * An accelerometer value on the {@link Controller} changed. The accelerometerCode is controller specific. The
	 * <code>com.badlogic.gdx.controllers.mapping</code> package hosts slider constants for known controllers. The value
	 * is a
	 * {@link Vector3} representing the acceleration on a 3-axis accelerometer in m/s^2.
	 *
	 * @param accelerometerCode
	 * @param value
	 * @return whether to hand the event to other listeners.
	 */
	boolean accelerometerMoved(int accelerometerCode, Vector3 value);
}
