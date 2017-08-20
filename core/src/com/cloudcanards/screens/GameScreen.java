package com.cloudcanards.screens;

import com.cloudcanards.algorithms.kd.KDTree;
import com.cloudcanards.assets.Assets;
import com.cloudcanards.box2d.MapCollisionBuilderTask;
import com.cloudcanards.box2d.WaterManager;
import com.cloudcanards.box2d.WorldContactListener;
import com.cloudcanards.camera.CameraFocus;
import com.cloudcanards.camera.LerpFocus;
import com.cloudcanards.camera.MultiCameraFocus;
import com.cloudcanards.character.TestChar;
import com.cloudcanards.console.Console;
import com.cloudcanards.graphics.RenderableManager;
import com.cloudcanards.grapple.Targetable;
import com.cloudcanards.input.ControllerManager;
import com.cloudcanards.loading.AbstractLoadAssetTask;
import com.cloudcanards.loading.AbstractTask;
import com.cloudcanards.loading.ResourceManager;
import com.cloudcanards.map.SpawnPointFinderTask;
import com.cloudcanards.map.SpecialTilesBuilderTask;
import com.cloudcanards.ui.FpsCounter;
import com.cloudcanards.ui.LogUI;
import com.cloudcanards.ui.MainHUD;
import com.cloudcanards.ui.VersionLabel;
import org.jetbrains.annotations.Nullable;

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
	@Nullable
	private CameraFocus cameraFocus;
	private MultiCameraFocus multiCameraFocus;
	private FitViewport viewport;
	private RenderableManager renderableManager;
	
	//map
	private String mapName;
	private TiledMap map;
	private OrthogonalTiledMapRenderer mapRenderer;
	private KDTree<Targetable> staticGrappleTargets;
	private Array<Targetable> dynamicGrappleTargets;
	private final Array<Vector2> spawnPoints;
	
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
		spawnPoints = new Array<>();
		
		batch = new SpriteBatch();
		camera = new OrthographicCamera();
		camera.position.x = 30;
		camera.position.y = 50;
		multiCameraFocus = new MultiCameraFocus(48 / 2f, 27 / 2f, 0, 0, 100, 100);
		cameraFocus = new LerpFocus(multiCameraFocus, 0.5f);
		viewport = new FitViewport(48, 27, camera);
		renderableManager = new RenderableManager();
		
		box2DDebugRenderer = new Box2DDebugRenderer(false, false, false, false, false, false);
		
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
				
				MapLayer spawnLayer = map.getLayers().get("spawnPoints");
				if (specialLayer != null)
				{
					resourceManager.addTask(new SpawnPointFinderTask(tileSize, specialLayer, spawnPoints));
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
		ControllerManager.getInstance().init();
	}
	
	@Override
	public void render(float delta)
	{
		//update
		player.update(delta);
		ControllerManager.getInstance().update(delta);
		
		waterManager.update(delta);
		boolean physics = doPhysicsStep(delta);
		
		//set camera
		if (cameraFocus != null)
		{
			cameraFocus.setPosition(camera);
			cameraFocus.setScale(camera);
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
	
	public CameraFocus getCameraFocus()
	{
		return cameraFocus;
	}
	
	public void addCameraFocus(CameraFocus cameraFocus)
	{
		multiCameraFocus.getFoci().add(cameraFocus);
	}
	
	public void removeCameraFocus(CameraFocus cameraFocus)
	{
		multiCameraFocus.getFoci().removeValue(cameraFocus, true);
	}
	
	public void setCameraFocus(CameraFocus cameraFocus)
	{
		this.cameraFocus = cameraFocus;
	}
	
	public RenderableManager getRenderableManager()
	{
		return renderableManager;
	}
	
	public KDTree<Targetable> getStaticGrappleTargets()
	{
		return staticGrappleTargets;
	}
	
	public Array<Targetable> getDynamicGrappleTargets()
	{
		return dynamicGrappleTargets;
	}
	
	public World getWorld()
	{
		return world;
	}
	
	private final Vector2 tempCoordsConversion2 = new Vector2();
	private final Vector3 tempCoordsConversion3 = new Vector3();
	
	/**
	 * Unwraps screen space coordinates to world coordinates
	 */
	public static Vector2 screenToWorldCoords(float x, float y)
	{
		if (getInstance() == null || getInstance().camera == null)
		{
			throw new RuntimeException("GameScreen or camera is null");
		}
		Vector3 unproject = getInstance().camera.unproject(getInstance().tempCoordsConversion3.set(x, y, 0));
		return getInstance().tempCoordsConversion2.set(unproject.x, unproject.y);
	}
	
	/**
	 * Unwraps world coordinates to screen space coordinates
	 */
	public static Vector2 worldToScreenCoords(float x, float y)
	{
		if (getInstance() == null || getInstance().camera == null)
		{
			throw new RuntimeException("GameScreen or camera is null");
		}
		Vector3 project = getInstance().camera.project(getInstance().tempCoordsConversion3.set(x, y, 0));
		return getInstance().tempCoordsConversion2.set(project.x, project.y);
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
	
	public Array<Vector2> getSpawnPoints()
	{
		return spawnPoints;
	}
}
