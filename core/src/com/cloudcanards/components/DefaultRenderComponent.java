package com.cloudcanards.components;

import com.cloudcanards.character.AbstractCharacter;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * DefaultRenderComponent
 *
 * @author creativitRy
 */
public class DefaultRenderComponent extends AbstractRenderableComponent
{
	public DefaultRenderComponent(AbstractCharacter character)
	{
		super(character);
	}
	
	@Override
	public void render(SpriteBatch batch, float delta)
	{
		TextureRegion frame;
		
		Vector2 pos = character.getBody().getPosition();
		
		frame = character.getAtlas().findRegion("idle");
		
		batch.draw(frame, pos.x - 0.5f, pos.y, 0.5f, 1f, 1, 2,
			character.isFaceRight() ? 1 : -1, 1, 0);
	}
}
