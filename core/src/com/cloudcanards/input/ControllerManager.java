package com.cloudcanards.input;

import com.cloudcanards.behavior.Updateable;
import com.cloudcanards.character.TestControllerChar;
import com.cloudcanards.character.components.ControllerInputComponent;
import com.cloudcanards.screens.GameScreen;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * ControllerManager
 *
 * @author creativitRy
 */
public class ControllerManager implements ControllerListener, Updateable
{
	private static final ControllerManager INSTANCE = new ControllerManager();
	
	public static ControllerManager getInstance()
	{
		return INSTANCE;
	}
	
	private final ObjectMap<Controller, ControllerInputListener> characters;
	private boolean isInit;
	private Array<Controller> temp;
	
	private ControllerManager()
	{
		Controllers.addListener(this);
		characters = new ObjectMap<>();
		isInit = false;
		temp = new Array<>();
	}
	
	/**
	 * Call after world and character texture are loaded
	 */
	public void init()
	{
		for (Controller controller : temp)
		{
			addChar(controller);
		}
		temp = null;
		isInit = true;
	}
	
	@Override
	public void connected(Controller controller)
	{
		if (!isInit)
		{
			temp.add(controller);
		}
		else
		{
			addChar(controller);
		}
	}
	
	private void addChar(Controller controller)
	{
		TestControllerChar character = new TestControllerChar(GameScreen.getInstance().getWorld(), controller);
		characters.put(controller, character.getControllerInputComponent());
	}
	
	@Override
	public void update(float delta)
	{
		for (ObjectMap.Entry<Controller, ControllerInputListener> character : characters)
		{
			((ControllerInputComponent) character.value).getCharacter().update(delta);
		}
	}
	
	@Override
	public void disconnected(Controller controller)
	{
		if (characters.containsKey(controller))
		{
			characters.get(controller).disconnected();
		}
	}
	
	@Override
	public boolean buttonDown(Controller controller, int buttonCode)
	{
		if (!characters.containsKey(controller))
		{
			addChar(controller);
		}
		return characters.get(controller).buttonDown(buttonCode);
	}
	
	@Override
	public boolean buttonUp(Controller controller, int buttonCode)
	{
		if (!characters.containsKey(controller))
		{
			addChar(controller);
		}
		return characters.get(controller).buttonUp(buttonCode);
	}
	
	@Override
	public boolean axisMoved(Controller controller, int axisCode, float value)
	{
		if (!characters.containsKey(controller))
		{
			addChar(controller);
		}
		return characters.get(controller).axisMoved(axisCode, value);
	}
	
	@Override
	public boolean povMoved(Controller controller, int povCode, PovDirection value)
	{
		if (!characters.containsKey(controller))
		{
			addChar(controller);
		}
		return characters.get(controller).povMoved(povCode, value);
	}
	
	@Override
	public boolean xSliderMoved(Controller controller, int sliderCode, boolean value)
	{
		if (!characters.containsKey(controller))
		{
			addChar(controller);
		}
		return characters.get(controller).xSliderMoved(sliderCode, value);
	}
	
	@Override
	public boolean ySliderMoved(Controller controller, int sliderCode, boolean value)
	{
		if (!characters.containsKey(controller))
		{
			addChar(controller);
		}
		return characters.get(controller).ySliderMoved(sliderCode, value);
	}
	
	@Override
	public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value)
	{
		if (!characters.containsKey(controller))
		{
			addChar(controller);
		}
		return characters.get(controller).accelerometerMoved(accelerometerCode, value);
	}
	
	public boolean isInit()
	{
		return isInit;
	}
}
