package com.cloudcanards.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.cloudcanards.CloudCanards;
import com.cloudcanards.assets.Assets;
import com.cloudcanards.io.loading.AbstractLoadAssetTask;
import com.cloudcanards.io.loading.ResourceManager;
import com.cloudcanards.ui.LoadingThrobber;

/**
 * LoadingScreen
 *
 * @author creativitRy
 */
public class LoadingScreen extends AbstractScreen
{
	private ResourceManager resourceManager;
	
	private AbstractScreen screen;
	
	private Stage stage;
	private LoadingThrobber throbber;
	
	
	public LoadingScreen(AbstractScreen screen)
	{
		resourceManager = CloudCanards.getInstance().getResourceManager();
		
		this.screen = screen;
		
		load(CloudCanards.getInstance().getResourceManager());
	}
	
	@Override
	public void load(ResourceManager resourceManager)
	{
		resourceManager.addTasks(new AbstractLoadAssetTask(resourceManager)
		{
			@Override
			public void run()
			{
				resourceManager.getAssetManager().load(Assets.DIR + Assets.THROBBER, TextureAtlas.class);
			}
			
			@Override
			public void postRun()
			{
				TextureAtlas temp = resourceManager.getAssetManager().get(Assets.DIR + Assets.THROBBER);
				Animation<TextureRegion> animation = new Animation<>(1 / 15f, temp.getRegions());
				throbber = new LoadingThrobber(animation);
				stage.addActor(throbber);
			}
		});
		
		//probably background image
		resourceManager.addTasks(new AbstractLoadAssetTask(resourceManager)
		{
			@Override
			public void run()
			{
				resourceManager.getAssetManager().load(Assets.DIR + "test.png", Texture.class);
			}
			
			@Override
			public void postRun()
			{
				Texture temp = resourceManager.getAssetManager().get(Assets.DIR + "test.png");
				final Image actor = new Image(temp);
				actor.setPosition(100, 100);
				stage.addActor(actor);
			}
		});
	}
	
	@Override
	public void show()
	{
		stage = new Stage(new FitViewport(LoadingScreen.SCREEN_WIDTH, LoadingScreen.SCREEN_HEIGHT));
		
		//todo
		Stack stack = new Stack();
		Table throbberTable = new Table();
	}
	
	@Override
	public void render(float delta)
	{
		resourceManager.update();
		stage.act(delta);
		stage.draw();
	}
	
	@Override
	public void resize(int width, int height)
	{
		stage.getViewport().update(width, height, true);
	}
	
	@Override
	public void pause()
	{
	
	}
	
	@Override
	public void resume()
	{
	
	}
	
	@Override
	public void dispose(ResourceManager manager)
	{
	
	}
}
