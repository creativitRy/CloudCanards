package com.cloudcanards.components;

import com.cloudcanards.assets.Assets;
import com.cloudcanards.behavior.Updateable;
import com.cloudcanards.character.AbstractCharacter;
import com.cloudcanards.grapple.Targetable;
import com.cloudcanards.input.InputAction;
import com.cloudcanards.input.InputListener;
import com.cloudcanards.input.InputManager;
import com.cloudcanards.input.InputType;
import com.cloudcanards.loading.AbstractLoadAssetTask;
import com.cloudcanards.loading.Loadable;
import com.cloudcanards.loading.ResourceManager;
import com.cloudcanards.screens.GameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * MouseGrappleComponent
 * Todo: extend from abstractinputgrapplecomponent that allows for both control by mouse and controller
 *
 * @author creativitRy
 */
public class MouseGrappleComponent extends AbstractRenderableComponent implements Loadable, Updateable, InputListener
{
	public static final float MIN_ANGLE = 2f;
	public static final int MAX_TARGET_DISTANCE = 20; //actual target distance is 50 * sqrt(2)
	private GrappleComponent grapple;
	
	private Texture targetTexture;
	
	private Array<Targetable> targets;
	private Targetable currentTarget;
	
	//both in radians
	private Vector2 unitVector;
	private float angle;
	private Vector2 snappedUnitVector;
	private float snappedAngle;
	
	public MouseGrappleComponent(AbstractCharacter character, GrappleComponent grapple, boolean enableInput)
	{
		super(character);
		this.grapple = grapple;
		
		mouseEnabled = enableInput;
		InputManager.register(this);
		
		targets = new Array<>();
		
		this.unitVector = new Vector2(1, 0);
		this.snappedUnitVector = new Vector2(1, 0);
		
		updateAngle(Gdx.input.getX(), Gdx.input.getY());
	}
	
	private void updateAngle(float mouseX, float mouseY)
	{
		Vector2 pos = GameScreen.screenToWorldCoords(mouseX, mouseY);
		this.angle = MathUtils.atan2(pos.y - character.getPosition().y, pos.x - character.getPosition().x);
		unitVector.setAngleRad(angle);
		
		//todo
		float bestAngle = 9999;
		int bestIndex = -1;
		
		for (int i = 0; i < targets.size; i++)
		{
			final float newAngle = MathUtils.atan2(targets.get(i).getPosition().y - character.getPosition().y,
				targets.get(i).getPosition().x - character.getPosition().x);
			if (Math.abs(newAngle - angle) < MIN_ANGLE && Math.abs(newAngle - angle) < Math.abs(bestAngle - angle))
			{
				bestAngle = newAngle;
				bestIndex = i;
			}
		}
		
		if (bestIndex == -1)
		{
			currentTarget = null;
			snappedAngle = angle;
		}
		else
		{
			currentTarget = targets.get(bestIndex);
			snappedAngle = bestAngle;
		}
		snappedUnitVector.setAngleRad(snappedAngle);
	}
	
	public void updateTargets()
	{
		//todo: change to camera viewport plus a bit of offset
		GameScreen.getInstance().getStaticGrappleTargets().getAllElementsInBox(targets,
			character.getPosition().x - MAX_TARGET_DISTANCE, character.getPosition().y - MAX_TARGET_DISTANCE,
			character.getPosition().x + MAX_TARGET_DISTANCE, character.getPosition().y + MAX_TARGET_DISTANCE);
		
		targets.addAll(GameScreen.getInstance().getDynamicGrappleTargets());
	}
	
	@Override
	public void load(ResourceManager resourceManager)
	{
		resourceManager.addTask(new AbstractLoadAssetTask()
		{
			@Override
			public void run()
			{
				resourceManager.getAssetManager().load(Assets.DIR + Assets.TARGET, Texture.class);
			}
			
			@Override
			public void postRun()
			{
				targetTexture = resourceManager.getAssetManager().get(Assets.DIR + Assets.TARGET);
			}
		});
	}
	
	@Override
	public void update(float delta)
	{
		if (!character.getBody().getLinearVelocity().equals(Vector2.Zero))
		{
			updateTargets();
		}
		updateAngle(Gdx.input.getX() - character.getPosition().x, Gdx.input.getY() - character.getPosition().y);
	}
	
	@Override
	public void render(SpriteBatch batch, float delta)
	{
		//draw on angle without snapping
		/*batch.draw(targetTexture, character.getPosition().x + unitVector.x - 0.5f,
			character.getPosition().y + unitVector.y - 0.5f, 1f, 1f);*/
		
		batch.draw(targetTexture, character.getPosition().x + snappedUnitVector.x - 0.5f,
			character.getPosition().y + snappedUnitVector.y - 0.5f, 1f, 1f);
	}
	
	@Override
	public int getZOrder()
	{
		return 10;
	}
	
	@Override
	public boolean onInput(InputAction action, InputType type, Object... args)
	{
		if (currentTarget == null)
		{
			return false;
		}
		if (action == InputAction.SHOOT)
		{
			if (args[0] instanceof Boolean && ((Boolean) args[0]))
			{
				grapple.shoot(currentTarget);
			}
			else
			{
				grapple.retract();
			}
			return true;
		}
		return false;
	}
	
	private boolean mouseEnabled;
	
	@Override
	public boolean isEnabled()
	{
		return mouseEnabled;
	}
	
	@Override
	public void enable()
	{
		mouseEnabled = true;
	}
	
	@Override
	public void disable()
	{
		mouseEnabled = false;
	}
}
