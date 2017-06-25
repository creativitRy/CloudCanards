package com.cloudcanards.components;

import com.cloudcanards.assets.Assets;
import com.cloudcanards.behavior.Updateable;
import com.cloudcanards.character.AbstractCharacter;
import com.cloudcanards.grapple.GrappleRope;
import com.cloudcanards.grapple.Targetable;
import com.cloudcanards.loading.AbstractLoadAssetTask;
import com.cloudcanards.loading.Loadable;
import com.cloudcanards.loading.ResourceManager;
import com.cloudcanards.screens.GameScreen;
import com.cloudcanards.util.ArrayUtil;
import com.cloudcanards.util.Vector2Comparator;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/**
 * GrappleComponent
 *
 * @author creativitRy
 */
public class GrappleComponent extends AbstractComponent implements Loadable, Updateable
{
	private GrappleRope rope;
	private NinePatch texture;
	
	public GrappleComponent(AbstractCharacter character)
	{
		super(character);
	}
	
	@Override
	public void load(ResourceManager resourceManager)
	{
		resourceManager.addTask(new AbstractLoadAssetTask()
		{
			@Override
			public void run()
			{
				resourceManager.getAssetManager().load(Assets.DIR + Assets.ROPE, TextureAtlas.class);
			}
			
			@Override
			public void postRun()
			{
				texture = resourceManager.getAssetManager().get(Assets.DIR + Assets.ROPE, TextureAtlas.class)
					.createPatch("white");
			}
		});
	}
	
	private float temp;
	
	@Override
	public void update(float delta)
	{
		if (rope != null)
		{
			rope.update(delta);
			
			
			return;
		}
		else
			temp += delta;
		
		if (temp > 1)
		{
			int index = ArrayUtil.binarySearch(GameScreen.getInstance().getGrappleTargets(),
				new Targetable()
				{
					@Override
					public Vector2 getPosition()
					{
						return character.getPosition();
					}
					
					@Override
					public Body getBody()
					{
						return null;
					}
				},
				(o1, o2) -> Vector2Comparator.compareStatic(o1.getPosition(), o2.getPosition()));
			if (index < 0)
				index = -index - 1;
			if (index == GameScreen.getInstance().getGrappleTargets().size)
				index = (int) (Math.random() * GameScreen.getInstance().getGrappleTargets().size);
			
			rope = new GrappleRope(this, GameScreen.getInstance().getGrappleTargets().get(index),
				GameScreen.getInstance().getWorld(), texture);
		}
	}
	
	public Vector2 getPosition()
	{
		return character.getPosition();
	}
	
	public Body getBody()
	{
		return character.getBody();
	}
}
