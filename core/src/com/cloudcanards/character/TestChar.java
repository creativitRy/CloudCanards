package com.cloudcanards.character;

import com.cloudcanards.assets.Assets;
import com.cloudcanards.camera.LerpFocus;
import com.cloudcanards.components.CameraFocusComponent;
import com.cloudcanards.components.DefaultRenderComponent;
import com.cloudcanards.components.InputComponent;
import com.cloudcanards.screens.GameScreen;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Yanna
 *
 * @author creativitRy
 */
public class TestChar extends AbstractCharacter
{
	public TestChar(World world, Vector2 position)
	{
		super(world, position, 1.5f, 32f / 32, Assets.DIR + "characters/test/player.atlas");
		
		addComponent(new DefaultRenderComponent(this));
		addComponent(new InputComponent(this, true));
		CameraFocusComponent camera = new CameraFocusComponent(this);
		GameScreen.getInstance().setCameraFocus(new LerpFocus(camera, 0.5f));
		addComponent(camera);
	}
}