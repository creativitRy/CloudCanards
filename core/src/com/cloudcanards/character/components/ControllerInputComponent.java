package com.cloudcanards.character.components;

import com.brokenbeach.controllers.Xbox360Controller;
import com.cloudcanards.character.AbstractCharacter;
import com.cloudcanards.input.ControllerInputListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;

/**
 * ControllerInputComponent
 *
 * @author creativitRy
 */
public class ControllerInputComponent extends AbstractInputComponent implements ControllerInputListener
{
	private static final float WALK_TRIGGER = 0.4f;
	private final ControllerCombatComponent controllerCombatComponent;
	
	@Nullable
	private Controller controller;
	private ControllerGrappleComponent controllerGrappleComponent;
	
	public ControllerInputComponent(AbstractCharacter character, @NotNull Controller controller,
									ControllerGrappleComponent controllerGrappleComponent,
									ControllerCombatComponent controllerCombatComponent)
	{
		super(character);
		
		this.controller = controller;
		
		this.controllerGrappleComponent = controllerGrappleComponent;
		this.controllerCombatComponent = controllerCombatComponent;
	}
	
	@Override
	public void disconnected()
	{
		character.damage(character.getHealth() + 1);
		controller = null;
	}
	
	@Override
	public boolean buttonDown(int buttonCode)
	{
		if (buttonCode == Xbox360Controller.BUTTON_A)
		{
			character.jump();
			return true;
		}
		if (buttonCode == Xbox360Controller.BUTTON_X)
		{
			controllerCombatComponent.onAttackInput(true);
			return true;
		}
		if (buttonCode == Xbox360Controller.BUTTON_LB)
		{
			setSprint(true);
			setSpeed();
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean buttonUp(int buttonCode)
	{
		if (buttonCode == Xbox360Controller.BUTTON_A)
		{
			character.stopJump();
			return true;
		}
		if (buttonCode == Xbox360Controller.BUTTON_X || buttonCode == Xbox360Controller.BUTTON_Y)
		{
			controllerCombatComponent.onAttackInput(false);
			return true;
		}
		if (buttonCode == Xbox360Controller.BUTTON_LB)
		{
			setSprint(false);
			setSpeed();
			return true;
		}
		
		return false;
	}
	
	@Override
	public boolean axisMoved(int axisCode, float value)
	{
		if (axisCode == Xbox360Controller.AXIS_LEFT_X)
		{
			setWalk(Math.abs(value) < WALK_TRIGGER);
			setSpeed();
			
			setLeft(value < -0.2f);
			setRight(value > 0.2f);
			setMovement();
			
			controllerCombatComponent.onAxisMoved(false, value);
			
			return true;
		}
		if (axisCode == Xbox360Controller.AXIS_LEFT_Y)
		{
			controllerCombatComponent.onAxisMoved(true, value);
			
			return true;
		}
		
		if (axisCode == Xbox360Controller.AXIS_RIGHT_X)
		{
			controllerGrappleComponent.setX(value);
			return true;
		}
		if (axisCode == Xbox360Controller.AXIS_RIGHT_Y)
		{
			controllerGrappleComponent.setY(-value);
			return true;
		}
		if (axisCode == Xbox360Controller.AXIS_LEFT_TRIGGER)
		{
			//left is positive, right is negative, don't use both together lel
			if (value < -.2f)
			{
				controllerGrappleComponent.shoot();
			}
			else
			{
				controllerGrappleComponent.retract();
			}
			
		}
		
		return false;
	}
	
	@Override
	public boolean povMoved(int povCode, PovDirection value)
	{
		return false;
	}
	
	@Override
	public boolean xSliderMoved(int sliderCode, boolean value)
	{
		return false;
	}
	
	@Override
	public boolean ySliderMoved(int sliderCode, boolean value)
	{
		return false;
	}
	
	@Override
	public boolean accelerometerMoved(int accelerometerCode, Vector3 value)
	{
		return false;
	}
	
	public boolean isControllerEnabled()
	{
		return controller != null;
	}
	
	@Nullable
	public Controller getController()
	{
		return controller;
	}
	
	public AbstractCharacter getCharacter()
	{
		return character;
	}
}
