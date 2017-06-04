package com.cloudcanards.ui;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import net.dermetfan.gdx.graphics.g2d.AnimatedSprite;

/**
 * Image that loops
 *
 * @author creativitRy
 */
public class LoadingThrobber extends Image
{
	
	private AnimatedSprite animation;
	
	public LoadingThrobber(Animation<TextureRegion> animation)
	{
		this(new AnimatedSprite(animation));
	}
	
	private LoadingThrobber(AnimatedSprite animation)
	{
		super(animation);
		animation.getAnimation().setPlayMode(Animation.PlayMode.LOOP);
		this.animation = animation;
	}
	
	@Override
	public void act(float delta)
	{
		super.act(delta);
		
		animation.update(delta);
	}
}
