package com.cloudcanards.character.components;

import com.cloudcanards.assets.Assets;
import com.cloudcanards.character.AbstractCharacter;
import com.cloudcanards.grapple.Targetable;
import com.cloudcanards.loading.AbstractLoadAssetTask;
import com.cloudcanards.loading.Loadable;
import com.cloudcanards.loading.ResourceManager;
import com.cloudcanards.screens.GameScreen;
import org.jetbrains.annotations.Nullable;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * AbstractGrappleControllerComponent
 *
 * @author creativitRy
 */
public abstract class AbstractGrappleControllerComponent extends AbstractRenderableComponent implements Loadable
{
	private GrappleComponent grapple;
	
	private Texture targetTexture;
	
	private final Array<Targetable> targets;
	@Nullable
	private Targetable currentTarget;
	
	//both in radians
	private Vector2 snappedUnitVector;
	
	public AbstractGrappleControllerComponent(AbstractCharacter character, GrappleComponent grapple)
	{
		super(character);
		this.grapple = grapple;
		
		targets = new Array<>();
		
		snappedUnitVector = new Vector2(1, 0);
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
	
	protected void updateTargets(float xMin, float yMin, float xMax, float yMax)
	{
		GameScreen.getInstance().getStaticGrappleTargets().getAllElementsInBox(getTargets(), xMin, yMin, xMax, yMax);
		
		for (Targetable targetable : GameScreen.getInstance().getDynamicGrappleTargets())
		{
			if (targetable.getPosition().x >= xMin && targetable.getPosition().x <= xMax &&
				targetable.getPosition().y >= yMin && targetable.getPosition().y <= yMax)
			{
				getTargets().add(targetable);
			}
		}
		//getTargets().addAll(GameScreen.getInstance().getDynamicGrappleTargets());
	}
	
	/**
	 * Called when new target is set
	 */
	protected void updateAngle()
	{
		if (currentTarget == null)
		{
			snappedUnitVector.set(0, 0);
			return;
		}
		
		snappedUnitVector.set(currentTarget.getPosition()).sub(character.getPosition()).nor();
		
	}
	
	@Override
	public void render(SpriteBatch batch, float delta)
	{
		batch.draw(targetTexture, character.getPosition().x + snappedUnitVector.x - 0.5f,
			character.getPosition().y + snappedUnitVector.y - 0.5f, 1f, 1f);
	}
	
	@Override
	public int getZOrder()
	{
		return 10;
	}
	
	public GrappleComponent getGrapple()
	{
		return grapple;
	}
	
	@Nullable
	public Targetable getCurrentTarget()
	{
		return currentTarget;
	}
	
	public void setCurrentTarget(@Nullable Targetable currentTarget)
	{
		this.currentTarget = currentTarget;
		updateAngle();
	}
	
	public Array<Targetable> getTargets()
	{
		return targets;
	}
}
