package com.cloudcanards.character;

import com.cloudcanards.CloudCanards;
import com.cloudcanards.assets.Assets;
import com.cloudcanards.character.components.*;
import com.cloudcanards.health.combat.CombatTeam;
import com.cloudcanards.screens.GameScreen;
import com.cloudcanards.util.Logger;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;

/**
 * TestControllerChar
 *
 * @author creativitRy
 */
public class TestControllerChar extends AbstractCharacter
{
	
	private final ControllerInputComponent controllerInputComponent;
	private final CameraFocusComponent camera;
	
	public TestControllerChar(World world, Controller controller)
	{
		super(world, GameScreen.getInstance().getSpawnPoints().random(), 1.5f, 32f / 32, Assets.DIR + "characters/test/player.atlas", CombatTeam.ETC);
		
		addComponent(new DefaultRenderComponent(this));
		camera = new CameraFocusComponent(this);
		addComponent(camera);
		GrappleComponent grappleComponent = new GrappleComponent(this, world);
		addComponent(grappleComponent);
		ControllerGrappleComponent controllerGrappleComponent = new ControllerGrappleComponent(this, grappleComponent);
		addComponent(controllerGrappleComponent);
		WeaponComponent weaponComponent = new WeaponComponent(this, world);
		addComponent(weaponComponent);
		ControllerCombatComponent controllerCombatComponent = new ControllerCombatComponent(this, weaponComponent);
		addComponent(controllerCombatComponent);
		controllerInputComponent = new ControllerInputComponent(this, controller, controllerGrappleComponent, controllerCombatComponent);
		addComponent(controllerInputComponent);
		//addComponent(new ScarfComponent(this, world));
		
		load(CloudCanards.getInstance().getResourceManager());
		CloudCanards.getInstance().getResourceManager().update();
		CloudCanards.getInstance().getResourceManager().update();
		CloudCanards.getInstance().getResourceManager().update();
		CloudCanards.getInstance().getResourceManager().update();
		
		onStart();
	}
	
	public void onStart()
	{
		GameScreen.getInstance().addCameraFocus(camera);
		resetHealth();
	}
	
	private boolean dead;
	
	@Override
	public void onDeath()
	{
		Logger.log("ded");
		GameScreen.getInstance().removeCameraFocus(camera);
		getGrappleComponent().retract();
		
		if (!dead)
		{
			dead = true;
			
			Timer.schedule(new Timer.Task()
			{
				@Override
				public void run()
				{
					Vector2 random = GameScreen.getInstance().getSpawnPoints().random();
					getBody().setLinearVelocity(0, 0);
					getBody().setTransform(random, 0);
					getGrappleComponent().getBody().setLinearVelocity(0, 0);
					getGrappleComponent().getBody().setTransform(random, 0);
					getBody().setLinearVelocity(0, 0);
					getBody().setTransform(random, 0);
					getGrappleComponent().getBody().setLinearVelocity(0, 0);
					getGrappleComponent().getBody().setTransform(random, 0);
					getBody().setLinearVelocity(0, 0);
					getBody().setTransform(random, 0);
					getGrappleComponent().getBody().setLinearVelocity(0, 0);
					getGrappleComponent().getBody().setTransform(random, 0);
					getBody().setLinearVelocity(0, 0);
					getBody().setTransform(random, 0);
					getGrappleComponent().getBody().setLinearVelocity(0, 0);
					getGrappleComponent().getBody().setTransform(random, 0);
					
					onStart();
					dead = false;
				}
			}, 3f);
		}
	}
	
	public ControllerInputComponent getControllerInputComponent()
	{
		return controllerInputComponent;
	}
}
