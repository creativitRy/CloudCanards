package com.cloudcanards.character;

import com.cloudcanards.CloudCanards;
import com.cloudcanards.assets.Assets;
import com.cloudcanards.character.components.*;
import com.cloudcanards.health.combat.CombatTeam;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * TestControllerChar
 *
 * @author creativitRy
 */
public class TestControllerChar extends AbstractCharacter
{
	
	private final ControllerInputComponent controllerInputComponent;
	
	public TestControllerChar(World world, Vector2 position, Controller controller)
	{
		super(world, position, 1.5f, 32f / 32, Assets.DIR + "characters/test/player.atlas", CombatTeam.ETC);
		
		addComponent(new DefaultRenderComponent(this));
		CameraFocusComponent camera = new CameraFocusComponent(this);
		addComponent(camera);//todo
		GrappleComponent grappleComponent = new GrappleComponent(this, world);
		addComponent(grappleComponent);
		ControllerGrappleComponent controllerGrappleComponent = new ControllerGrappleComponent(this, grappleComponent);
		addComponent(controllerGrappleComponent);
		controllerInputComponent = new ControllerInputComponent(this, controller, controllerGrappleComponent);
		addComponent(controllerInputComponent);
		//addComponent(new ScarfComponent(this, world));
		
		load(CloudCanards.getInstance().getResourceManager());
		CloudCanards.getInstance().getResourceManager().update();
		CloudCanards.getInstance().getResourceManager().update();
		CloudCanards.getInstance().getResourceManager().update();
		CloudCanards.getInstance().getResourceManager().update();
	}
	
	@Override
	public void onDeath()
	{
	
	}
	
	public ControllerInputComponent getControllerInputComponent()
	{
		return controllerInputComponent;
	}
}
