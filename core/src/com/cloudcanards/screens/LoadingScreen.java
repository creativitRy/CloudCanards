package com.cloudcanards.screens;

import com.cloudcanards.CloudCanards;
import com.cloudcanards.assets.Assets;
import com.cloudcanards.loading.AbstractLoadAssetTask;
import com.cloudcanards.loading.ResourceManager;
import com.cloudcanards.ui.LoadingThrobber;
import com.cloudcanards.util.Logger;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * LoadingScreen
 *
 * @author creativitRy
 */
public class LoadingScreen extends AbstractScreen
{
	private ResourceManager resourceManager;
	
	//loading
	private AbstractScreen screen;
	/**
	 * 0 = Nothing is loaded yet
	 * 1 = LoadingScreen loaded
	 * 2 = Next screen loaded
	 */
	private int loadingState;
	
	//rendering
	private Stage stage;
	private LoadingThrobber throbber;
	
	
	public LoadingScreen(AbstractScreen screen)
	{
		resourceManager = CloudCanards.getInstance().getResourceManager();
		
		this.screen = screen;
		loadingState = 0;
		
		load(CloudCanards.getInstance().getResourceManager());
	}
	
	@Override
	public void load(ResourceManager resourceManager)
	{
		resourceManager.addTask(new AbstractLoadAssetTask(resourceManager)
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
		resourceManager.addTask(new AbstractLoadAssetTask(resourceManager)
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
				final Image image = new Image(temp);
				image.setPosition(100, 100);
				
				image.getColor().a = 0;
				image.addAction(Actions.fadeIn(3f));
				
				stage.addActor(image);
			}
		});
	}
	
	@Override
	public void show()
	{
		stage = new Stage(new FitViewport(LoadingScreen.SCREEN_WIDTH, LoadingScreen.SCREEN_HEIGHT));
		final Color color = stage.getBatch().getColor();
		stage.getBatch().setColor(color.r, color.g, color.b, 0.5f);
		
		//todo
		Stack stack = new Stack();
		Table throbberTable = new Table();
	}
	
	@Override
	public void render(float delta)
	{
		if (resourceManager.update())
		{
			loadingState++;
			if (loadingState == 1)
			{
				Logger.log("loading " + screen.getClass().getSimpleName());
				screen.load(CloudCanards.getInstance().getResourceManager());
			}
			else if (loadingState > 2) // move to next screen one frame after loading everything
			{
				CloudCanards.getInstance().setScreen(screen);
			}
		}
		
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
	public void dispose(ResourceManager resourceManager)
	{
		resourceManager.getAssetManager().unload(Assets.DIR + Assets.THROBBER);
		resourceManager.getAssetManager().unload(Assets.DIR + "test.png");
	}
}
