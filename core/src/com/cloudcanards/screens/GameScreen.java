package com.cloudcanards.screens;

import com.cloudcanards.algorithms.kd.KDTree;
import com.cloudcanards.assets.Assets;
import com.cloudcanards.box2d.MapCollisionBuilderTask;
import com.cloudcanards.box2d.WaterManager;
import com.cloudcanards.box2d.WorldContactListener;
import com.cloudcanards.camera.CameraFocus;
import com.cloudcanards.character.TestChar;
import com.cloudcanards.console.Console;
import com.cloudcanards.graphics.RenderableManager;
import com.cloudcanards.grapple.Targetable;
import com.cloudcanards.loading.AbstractLoadAssetTask;
import com.cloudcanards.loading.AbstractTask;
import com.cloudcanards.loading.ResourceManager;
import com.cloudcanards.map.SpecialTilesBuilderTask;
import com.cloudcanards.ui.FpsCounter;
import com.cloudcanards.ui.LogUI;
import com.cloudcanards.ui.MainHUD;
import com.cloudcanards.ui.VersionLabel;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
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
	private RenderableManager renderableManager;
	
	//map
	private String mapName;
	private TiledMap map;
	private OrthogonalTiledMapRenderer mapRenderer;
	private KDTree<Targetable> staticGrappleTargets;
	private Array<Targetable> dynamicGrappleTargets;
	
	//box2d
	private World world;
	private float physicsTick;
	private WaterManager waterManager;
	
	private Box2DDebugRenderer box2DDebugRenderer;
	
	//entity
	private TestChar player;
	
	//ui
	private Stage uiStage;
	
	public GameScreen(String mapName)
	{
		INSTANCE = this;
		
		this.mapName = mapName;
		staticGrappleTargets = new KDTree<Targetable>()
		{
			@Override
			protected Vector2 getPosition(Targetable element)
			{
				return element.getPosition();
			}
		};
		dynamicGrappleTargets = new Array<>();
		
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.position.x = 30;
		camera.position.y = 50;
		viewport = new FitViewport(48, 27, camera);
		renderableManager = new RenderableManager();
		
		box2DDebugRenderer = new Box2DDebugRenderer();
		
		uiStage = new Stage(new FitViewport(SCREEN_WIDTH, SCREEN_HEIGHT));
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
				waterManager = new WaterManager(world);
				player = new TestChar(world, new Vector2(20, 70));
				player.load(resourceManager);
				renderableManager.add(player);
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
				resourceManager.getAssetManager().load(Assets.DIR + Assets.LEVEL_DIR + mapName, TiledMap.class, params);
			}
			
			@Override
			public void postRun()
			{
				map = resourceManager.getAssetManager().get(Assets.DIR + Assets.LEVEL_DIR + mapName);
				
				float tileSize = map.getProperties().get("tilewidth", 32, Integer.class);
				mapRenderer = new OrthogonalTiledMapRenderer(map, 1 / tileSize, batch);
				
				MapLayer collisionLayer = map.getLayers().get("collision");
				if (collisionLayer != null)
				{
					resourceManager.addTask(new MapCollisionBuilderTask(tileSize, collisionLayer,
						world));
				}
				
				MapLayer specialLayer = map.getLayers().get("special");
				if (specialLayer != null)
				{
					resourceManager.addTask(new SpecialTilesBuilderTask(tileSize, specialLayer, world));
				}
			}
		});
		
		//todo
		initUi();
	}
	
	private void initUi()
	{
		uiStage.addActor(new MainHUD());
		uiStage.addActor(new FpsCounter(Align.topRight));
		uiStage.addActor(new VersionLabel(Align.bottomRight));
		uiStage.addActor(new LogUI(Align.bottomLeft).addChangePoster(new LogUI.ChangePoster()
		{
			@Override
			protected String getValue()
			{
				return player.getStateMachine().getCurrentState().name();
			}
		}));
		uiStage.addActor(Console.getInstance().ui());
	}
	
	@Override
	public void show()
	{
	}
	
	@Override
	public void render(float delta)
	{
		//update
		player.update(delta);
		
		waterManager.update(delta);
		boolean physics = doPhysicsStep(delta);
		
		//set camera
		if (focus != null)
		{
			focus.setPosition(camera);
		}
		camera.update();
		viewport.apply();
		batch.setProjectionMatrix(camera.combined);
		
		//render
		mapRenderer.setView(camera);
		mapRenderer.render();
		
		renderableManager.render(batch, delta);
		
		box2DDebugRenderer.render(world, camera.combined);
		uiStage.act(delta);
		uiStage.draw();
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
		physicsTick += deltaTime;
		
		if (physicsTick < TIME_STEP)
		{
			return false;
		}
		
		while (physicsTick >= TIME_STEP)
		{
			player.physicsStep();
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
		//todo: unregister inputlisteners here
		uiStage.dispose();
		box2DDebugRenderer.dispose();
	}
	
	public void setCameraFocus(CameraFocus cameraFocus)
	{
		this.focus = cameraFocus;
	}
	
	public RenderableManager getRenderableManager()
	{
		return renderableManager;
	}
	
	public KDTree<Targetable> getStaticGrappleTargets()
	{
		return staticGrappleTargets;
	}
	
	public void setStaticGrappleTargets(KDTree<Targetable> staticGrappleTargets)
	{
		this.staticGrappleTargets = staticGrappleTargets;
	}
	
	public Array<Targetable> getDynamicGrappleTargets()
	{
		return dynamicGrappleTargets;
	}
	
	public World getWorld()
	{
		return world;
	}
	
	/**
	 * Unwraps screen space coordinates to world coordinates
	 *
	 * @param x
	 * @param y
	 * @return null if camera or instance is null, world space otherwise
	 */
	public static Vector2 screenToWorldCoords(float x, float y)
	{
		if (getInstance() == null || getInstance().camera == null)
		{
			return null;
		}
		Vector3 unproject = getInstance().camera.unproject(new Vector3(x, y, 0));
		return new Vector2(unproject.x, unproject.y);
	}
	
	public void addUiActor(Actor actor)
	{
		uiStage.addActor(actor);
	}
	
	public Stage getUiStage()
	{
		return uiStage;
	}
	
	public TestChar getPlayer()
	{
		//todo: return character with input component, if any
		return player;
	}
	
	public OrthographicCamera getCamera()
	{
		return camera;
	}
}
