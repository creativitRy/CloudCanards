package com.cloudcanards.character;

import com.cloudcanards.assets.Assets;
import com.cloudcanards.character.components.*;
import com.cloudcanards.health.combat.CombatTeam;
import com.cloudcanards.screens.GameScreen;
import com.cloudcanards.util.Logger;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;

/**
 * Yanna
 *
 * @author creativitRy
 */
public class TestChar extends AbstractCharacter
{
	private final CameraFocusComponent camera;
	
	public TestChar(World world, Vector2 position)
	{
		super(world, position, 1.5f, 32f / 32, Assets.DIR + "characters/test/player.atlas", CombatTeam.ALLY);
		
		addComponent(new DefaultRenderComponent(this));
		addComponent(new InputComponent(this, true));
		camera = new CameraFocusComponent(this);
		//GameScreen.getInstance().setCameraFocus(new LerpFocus(camera, 0.5f));
		addComponent(camera);
		GrappleComponent grappleComponent = new GrappleComponent(this, world);
		addComponent(grappleComponent);
		addComponent(new MouseGrappleComponent(this, grappleComponent, true));
		//addComponent(new ScarfComponent(this, world));
		WeaponComponent weaponComponent = new WeaponComponent(this, world);
		addComponent(weaponComponent);
		addComponent(new MouseCombatComponent(this, weaponComponent, true));
		
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
		Logger.log("Ded");
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
					System.out.println("d");
					getBody().setLinearVelocity(0, 0);
					getBody().setTransform(GameScreen.getInstance().getSpawnPoints().random(), 0);
					
					getGrappleComponent().getBody().setLinearVelocity(0, 0);
					getGrappleComponent().getBody().setTransform(getPosition(), 0);
					
					onStart();
					dead = false;
				}
			}, 3f);
		}
	}
}
