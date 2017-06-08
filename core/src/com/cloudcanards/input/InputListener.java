package com.cloudcanards.input;

/**
 * Call InputManager.getInstance().addListener() to add listener
 *
 * @author creativitRy
 */
public interface InputListener
{
	/**
	 * Called when input is triggered
	 *
	 * @param action action
	 * @param type   what kind of input is this
	 * @param args   arguments
	 *               <p>
	 *               if type is a key/mouse/controller button - first arg is true if button was pressed and false if
	 *               released
	 *               <p>
	 *               if type is controller - next arg is controller
	 *               <p>
	 *               if type is mouse button - next two args are screen x and y position
	 *               <p>
	 *               if type is axis - next arg is axis position in range [-1, 1]
	 * @return true if input was processed (stops other listeners from dealing with this input)
	 */
	boolean onInput(InputAction action, InputType type, Object... args);
	
	boolean isEnabled();
	
	void enable();
	
	void disable();
}
