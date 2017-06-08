package com.cloudcanards.character;

import com.cloudcanards.assets.Assets;
import com.cloudcanards.components.DefaultRenderComponent;
import com.cloudcanards.components.InputComponent;

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
		super(world, position, 64f / 32, 32f / 32, Assets.DIR + "characters/test/player.atlas");
		
		addComponent(new DefaultRenderComponent(this));
		addComponent(new InputComponent(this, true));
	}
}
