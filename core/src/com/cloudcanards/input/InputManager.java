package com.cloudcanards.input;

import com.cloudcanards.util.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.controllers.mappings.Xbox;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

import java.util.EnumMap;
import java.util.Iterator;

/**
 * InputManager
 *
 * @author creativitRy
 */
public class InputManager implements InputProcessor, ControllerListener
{
	private static transient InputManager INSTANCE;
	
	public static InputManager getInstance()
	{
		return INSTANCE;
	}
	
	public static void init()
	{
		INSTANCE = InputIO.load();
	}
	
	public static void register(InputListener listener)
	{
		getInstance().addListener(listener);
	}
	
	public static boolean isEnabled()
	{
		return getInstance().enabled;
	}
	
	public static void setEnabled(boolean enabled)
	{
		getInstance().enabled = enabled;
		if (enabled)
		{
			Gdx.input.setInputProcessor(getInstance());
		}
	}
	
	private boolean enabled;
	
	//maps
	private ObjectMap<Integer, InputAction> keyMap;
	private ObjectMap<Integer, InputAction> mouseButtonMap;
	private ObjectMap<Integer, InputAction> mouseMoveMap;
	private ObjectMap<Integer, InputAction> mouseScrollMap;
	private ObjectMap<Integer, InputAction> controllerButtonMap;
	private ObjectMap<Integer, InputAction> constrollerAxisMap;
	private ObjectMap<Integer, InputAction> controllerPOVMap;
	private ObjectMap<Integer, InputAction> controllerTriggerMap;
	
	private EnumMap<InputAction, Boolean> currentInputs;
	
	private transient Array<InputListener> listeners;
	private transient Array<InputListener> removalPendingListeners; //this is an array bc listeners might not have
	// unique hashes
	
	public InputManager()
	{
		Gdx.input.setInputProcessor(this);
		Controllers.addListener(this);
		enabled = true;
		
		listeners = new Array<>(16);
		removalPendingListeners = new Array<>(4);
		
		currentInputs = new EnumMap<>(InputAction.class);
		for (InputAction inputAction : InputAction.values())
		{
			currentInputs.put(inputAction, false);
		}
	}
	
	/**
	 * Does not check for duplicates
	 *
	 * @param listener
	 */
	public void addListener(InputListener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 * Make sure this is called from within the inputlistener's onInput to avoid multithreading problems
	 *
	 * @param listener listener to remove (checked by == not equals)
	 */
	public void removeListener(InputListener listener)
	{
		removalPendingListeners.add(listener);
	}
	
	private boolean notifyListeners(InputAction action, InputType type, Object... args)
	{
		//remove pending listeners
		if (removalPendingListeners.size != 0)
		{
			Iterator<InputListener> it = removalPendingListeners.iterator();
			while (it.hasNext())
			{
				InputListener listener = it.next();
				if (listeners.contains(listener, true))
				{
					it.remove();
					listeners.removeValue(listener, true);
				}
			}
		}
		
		if (enabled)
		{
			for (int i = listeners.size - 1; i >= 0; i--)
			{
				InputListener listener = listeners.get(i);
				if (listener.isEnabled() && listener.onInput(action, type, args))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	public boolean isPressed(InputAction inputAction)
	{
		return currentInputs.get(inputAction);
	}
	
	@Override
	public boolean keyDown(int keycode)
	{
		if (keyMap.containsKey(keycode))
		{
			currentInputs.put(keyMap.get(keycode), true);
			return notifyListeners(keyMap.get(keycode), InputType.KEYBOARD_KEY, true);
		}
		
		Logger.log(keycode);
		return false;
	}
	
	@Override
	public boolean keyUp(int keycode)
	{
		if (keyMap.containsKey(keycode))
		{
			currentInputs.put(keyMap.get(keycode), false);
			return notifyListeners(keyMap.get(keycode), InputType.KEYBOARD_KEY, false);
		}
		return false;
	}
	
	@Override
	public boolean keyTyped(char character)
	{
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button)
	{
		if (mouseButtonMap.containsKey(button))
		{
			currentInputs.put(mouseButtonMap.get(button), true);
			return notifyListeners(mouseButtonMap.get(button), InputType.MOUSE_BUTTON, true, screenX, screenY);
		}
		return false;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button)
	{
		if (mouseButtonMap.containsKey(button))
		{
			currentInputs.put(mouseButtonMap.get(button), false);
			return notifyListeners(mouseButtonMap.get(button), InputType.MOUSE_BUTTON, false, screenX, screenY);
		}
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer)
	{
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY)
	{
		return false;
	}
	
	@Override
	public boolean scrolled(int amount)
	{
		return false;
	}
	
	@Override
	public void connected(Controller controller)
	{
	
	}
	
	@Override
	public void disconnected(Controller controller)
	{
	
	}
	
	@Override
	public boolean buttonDown(Controller controller, int buttonCode)
	{
		Logger.logAll(controller.getName(), buttonCode);
		if (controllerButtonMap.containsKey(buttonCode))
		{
			//todo - based on controller name
			buttonCode = (buttonCode << 3) & 0b001;
			return notifyListeners(controllerButtonMap.get(buttonCode), InputType.CONTROLLER_BUTTON, true);
		}
		return false;
	}
	
	@Override
	public boolean buttonUp(Controller controller, int buttonCode)
	{
		if (controllerButtonMap.containsKey(buttonCode))
		{
			return notifyListeners(controllerButtonMap.get(buttonCode), InputType.CONTROLLER_BUTTON, false);
		}
		return false;
	}
	
	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value)
	{
		return false;
	}
	
	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value)
	{
		if (Xbox.isXboxController(controller))
		{
		
		}
		
		return false;
	}
	
	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value)
	{
		return false;
	}
	
	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value)
	{
		return false;
	}
	
	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value)
	{
		return false;
	}
	
	//below is only for serialization
	ObjectMap<Integer, InputAction> getKeyMap()
	{
		return keyMap;
	}
	
	void setKeyMap(ObjectMap<Integer, InputAction> keyMap)
	{
		this.keyMap = keyMap;
	}
	
	ObjectMap<Integer, InputAction> getMouseButtonMap()
	{
		return mouseButtonMap;
	}
	
	void setMouseButtonMap(ObjectMap<Integer, InputAction> mouseButtonMap)
	{
		this.mouseButtonMap = mouseButtonMap;
	}
	
	ObjectMap<Integer, InputAction> getMouseMoveMap()
	{
		return mouseMoveMap;
	}
	
	void setMouseMoveMap(ObjectMap<Integer, InputAction> mouseMoveMap)
	{
		this.mouseMoveMap = mouseMoveMap;
	}
	
	ObjectMap<Integer, InputAction> getMouseScrollMap()
	{
		return mouseScrollMap;
	}
	
	void setMouseScrollMap(ObjectMap<Integer, InputAction> mouseScrollMap)
	{
		this.mouseScrollMap = mouseScrollMap;
	}
	
	ObjectMap<Integer, InputAction> getControllerButtonMap()
	{
		return controllerButtonMap;
	}
	
	void setControllerButtonMap(ObjectMap<Integer, InputAction> controllerButtonMap)
	{
		this.controllerButtonMap = controllerButtonMap;
	}
	
	ObjectMap<Integer, InputAction> getConstrollerAxisMap()
	{
		return constrollerAxisMap;
	}
	
	void setConstrollerAxisMap(ObjectMap<Integer, InputAction> constrollerAxisMap)
	{
		this.constrollerAxisMap = constrollerAxisMap;
	}
	
	ObjectMap<Integer, InputAction> getControllerPOVMap()
	{
		return controllerPOVMap;
	}
	
	void setControllerPOVMap(ObjectMap<Integer, InputAction> controllerPOVMap)
	{
		this.controllerPOVMap = controllerPOVMap;
	}
	
	ObjectMap<Integer, InputAction> getControllerTriggerMap()
	{
		return controllerTriggerMap;
	}
	
	void setControllerTriggerMap(ObjectMap<Integer, InputAction> controllerTriggerMap)
	{
		this.controllerTriggerMap = controllerTriggerMap;
	}
}
