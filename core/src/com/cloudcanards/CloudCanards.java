package com.cloudcanards;

import com.cloudcanards.assets.Assets;
import com.cloudcanards.input.InputManager;
import com.cloudcanards.loading.ResourceManager;
import com.cloudcanards.screens.GameScreen;
import com.cloudcanards.screens.LoadingScreen;
import com.cloudcanards.time.TimeManager;
import com.cloudcanards.util.Logger;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.SkinLoader;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.physics.box2d.Box2D;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Timer;

/**
 * CloudCanards
 *
 * @author creativitRy
 */
public class CloudCanards extends Game
{
	private static final CloudCanards INSTANCE = new CloudCanards();
	private static final float MAX_DELTA_TIME = 1f / 20f; //20 fps or below slows down the game instead of skipping frames
	private Skin skin;
	
	public static CloudCanards getInstance()
	{
		return INSTANCE;
	}
	
	private ResourceManager resourceManager;
	
	/**
	 * Don't put anything here. Put it in create() instead
	 */
	private CloudCanards() {}
	
	@Override
	public void create()
	{
		resourceManager = new ResourceManager();
		
		//todo: move to loading screen after starting splash screen - make sure this is called before getInstance()
		InputManager.init();
		
		//todo: move to loading screen after starting splash screen
		
		Timer.schedule(new Timer.Task()
		{
			@Override
			public void run()
			{
				FreetypeFontLoader.FreeTypeFontLoaderParameter param = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
				param.fontFileName = Assets.DIR + Assets.FONT; //this determines the actual path to the font
				param.fontParameters.size = 40;
				//the file name below can be any arbitrary string that ends with .ttf (needed when loading same font with different sizes)
				resourceManager.getAssetManager().load("normal.ttf", BitmapFont.class, param);
				
				param = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
				param.fontFileName = Assets.DIR + Assets.FONT; //this determines the actual path to the font
				param.fontParameters.size = 20;
				resourceManager.getAssetManager().load("small.ttf", BitmapFont.class, param);
				
				resourceManager.getAssetManager().finishLoading();
				
				ObjectMap<String, Object> fontMap = new ObjectMap<>();
				fontMap.put("normal", resourceManager.getAssetManager().get("normal.ttf"));
				fontMap.put("small", resourceManager.getAssetManager().get("small.ttf"));
				
				resourceManager.getAssetManager().load(Assets.DIR + Assets.SKIN, Skin.class, new SkinLoader.SkinParameter(fontMap));
				
				resourceManager.getAssetManager().finishLoading();
				skin = resourceManager.getAssetManager().get(Assets.DIR + Assets.SKIN);
				
			}
		}, 0.01f);
		
		//todo: delete once there aren't any temporary assets
		Assets.checkTempAssets();
		
		//todo: move to starting game
		Box2D.init();
		
		setScreen(new LoadingScreen(new GameScreen("dev/steampunk.tmx")));
	}
	
	public ResourceManager getResourceManager()
	{
		return resourceManager;
	}
	
	@Override
	public void setScreen(Screen screen)
	{
		super.setScreen(screen);
		Logger.log("Setting screen to " + screen.getClass().getSimpleName());
	}
	
	@Override
	public void render()
	{
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		TimeManager.getInstance().setDelta(Gdx.graphics.getDeltaTime());
		
		//super.render();
		if (screen != null)
		{
			screen.render(Math.min(Gdx.graphics.getDeltaTime(), MAX_DELTA_TIME));
		}
	}
	
	@Override
	public void dispose()
	{
		resourceManager.dispose();
	}
	
	public Skin getSkin()
	{
		if (skin == null)
			throw new NullPointerException("Skin is not loaded yet");
		return skin;
	}
}
