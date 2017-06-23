package com.cloudcanards.screens;

import com.cloudcanards.assets.Assets;
import com.cloudcanards.box2d.MapCollisionBuilderTask;
import com.cloudcanards.box2d.WorldContactListener;
import com.cloudcanards.camera.CameraFocus;
import com.cloudcanards.character.TestChar;
import com.cloudcanards.loading.AbstractLoadAssetTask;
import com.cloudcanards.loading.AbstractTask;
import com.cloudcanards.loading.ResourceManager;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * GameScreen
 *
 * @author creativitRy
 */
public class GameScreen extends AbstractScreen
{
	private static GameScreen INSTANCE;
	
	public static GameScreen getInstance()
	{
		return INSTANCE;
	}
	
	public static final Vector2 GRAVITY = new Vector2(0, -30);
	private static final float TIME_STEP = 1f / 300f;
	private static final int VELOCITY_ITERATIONS = 6;
	private static final int POSITION_ITERATIONS = 2;
	
	//render
	private SpriteBatch batch;
	private OrthographicCamera camera;
	private CameraFocus focus;
	private FitViewport viewport;
	
	//map
	private String mapName;
	private TiledMap map;
	private OrthogonalTiledMapRenderer mapRenderer;
	
	//box2d
	private World world;
	private float physicsTick;
	//todo
	private Box2DDebugRenderer box2DDebugRenderer;
	private TestChar player;
	
	public GameScreen(String mapName)
	{
		INSTANCE = this;
		this.mapName = mapName;
		
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.position.x = 30;
		camera.position.y = 50;
		viewport = new FitViewport(48, 27, camera);
		
		box2DDebugRenderer = new Box2DDebugRenderer();
	}
	
	@Override
	public void load(ResourceManager resourceManager)
	{
		//init world
		resourceManager.addTask(new AbstractTask()
		{
			@Override
			public void run()
			{
				world = new World(GRAVITY, true);
				world.setContactListener(new WorldContactListener());
				player = new TestChar(world, new Vector2(20, 70));
				player.load(resourceManager);
				finish();
			}
		});
		
		
		//load map
		resourceManager.addTask(new AbstractLoadAssetTask()
		{
			@Override
			public void run()
			{
				TmxMapLoader.Parameters params = new TmxMapLoader.Parameters();
				params.textureMinFilter = Texture.TextureFilter.Linear;
				params.textureMagFilter = Texture.TextureFilter.Nearest;
				resourceManager.getAssetManager().load(Assets.DIR + mapName, TiledMap.class, params);
			}
			
			@Override
			public void postRun()
			{
				map = resourceManager.getAssetManager().get(Assets.DIR + mapName);
				
				float tileSize = map.getProperties().get("tilewidth", 32, Integer.class);
				mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / tileSize, batch);
				
				MapLayer collisionLayer = map.getLayers().get("collision");
				if (collisionLayer != null)
				{
					resourceManager.addTask(new MapCollisionBuilderTask(tileSize, collisionLayer,
						world));
				}
			}
		});
	}
	
	@Override
	public void show()
	{
	
	}
	
	public void setCameraFocus(CameraFocus cameraFocus)
	{
		this.focus = cameraFocus;
	}
	
	@Override
	public void render(float delta)
	{
		//update
		player.update(delta);
		
		boolean physics = doPhysicsStep(delta);
		
		//set camera
		if (focus != null)
			focus.setPosition(camera);
		camera.update();
		viewport.apply();
		batch.setProjectionMatrix(camera.combined);
		
		//render
		mapRenderer.setView(camera);
		mapRenderer.render();
		
		batch.begin();
		player.render(batch, delta);
		batch.end();
		
		box2DDebugRenderer.render(world, camera.combined);
	}
	
	/**
	 * Updates the physics world while
	 * making sure dt in the physics simulation is constant
	 *
	 * @param deltaTime time between each frame (might not be constant)
	 * @return new value of physicsStepped indicating whether the world updated or not
	 */
	private boolean doPhysicsStep(float deltaTime)
	{
		// max frame time to avoid spiral of death (on slow devices)
		float frameTime = Math.min(deltaTime, 0.25f);
		physicsTick += frameTime;
		
		if (physicsTick < TIME_STEP)
			return false;
		
		while (physicsTick >= TIME_STEP)
		{
			world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
			physicsTick -= TIME_STEP;
		}
		
		return true;
	}
	
	@Override
	public void resize(int width, int height)
	{
		viewport.update(width, height, false);
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
	
	}
}
