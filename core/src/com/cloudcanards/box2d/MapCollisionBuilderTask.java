package com.cloudcanards.box2d;

import com.cloudcanards.io.loading.AbstractTask;
import com.cloudcanards.io.loading.ResourceManager;

import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

/**
 * MapCollisionBuilder
 *
 * @author creativitRy
 */
public class MapCollisionBuilderTask extends AbstractTask
{
	private float tileSize;
	private MapLayer collisionLayer;
	private World world;
	
	private Array<Body> collisions;
	
	/**
	 * @param collisionLayer tiled object layer to extract the collision data from
	 * @param tileSize       the size of one tile in pixels (if your tiles are 16x16, this is set to 16f)
	 * @param world          box2d world to create bodies with
	 */
	public MapCollisionBuilderTask(ResourceManager resourceManager, float tileSize, MapLayer collisionLayer, World world)
	{
		super(resourceManager);
		this.tileSize = tileSize;
		this.collisionLayer = collisionLayer;
		this.world = world;
	}
	
	@Override
	public void run()
	{
		collisions = buildShapes();
		finish();
	}
	
	/**
	 * Builds the box2d static bodies for collision data within the level
	 *
	 * @return array of static bodies
	 */
	public Array<Body> buildShapes()
	{
		MapObjects objects = collisionLayer.getObjects();
		
		Array<Body> bodies = new Array<Body>();
		
		for (MapObject object : objects)
		{
			
			if (object instanceof TextureMapObject || !object.isVisible())
			{
				continue;
			}
			
			Shape shape;
			
			if (object instanceof RectangleMapObject)
			{
				shape = getRectangle((RectangleMapObject) object);
			}
			else if (object instanceof PolygonMapObject)
			{
				shape = getPolygon((PolygonMapObject) object);
			}
			else if (object instanceof PolylineMapObject)
			{
				shape = getPolyline((PolylineMapObject) object);
			}
			else if (object instanceof CircleMapObject)
			{
				shape = getCircle((CircleMapObject) object);
			}
			else
			{
				continue;
			}
			
			BodyDef bd = new BodyDef();
			bd.type = BodyDef.BodyType.StaticBody;
			Body body = world.createBody(bd);
			
			FixtureDef fixture = new FixtureDef();
			fixture.shape = shape;
			
			Float friction = object.getProperties().get("friction", 1f, Float.class);
			Float restitution = object.getProperties().get("restitution", Float.class);
			Float density = object.getProperties().get("density", Float.class);
			Boolean sensor = object.getProperties().get("sensor", Boolean.class);
			
			fixture.friction = friction;
			
			if (restitution != null)
				fixture.restitution = restitution;
			if (density != null)
				fixture.density = density;
			if (sensor != null)
				fixture.isSensor = sensor;
			
			body.createFixture(fixture);
			
			bodies.add(body);
			
			shape.dispose();
		}
		return bodies;
	}
	
	private PolygonShape getRectangle(RectangleMapObject rectangleObject)
	{
		Rectangle rectangle = rectangleObject.getRectangle();
		PolygonShape polygon = new PolygonShape();
		Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) / tileSize,
			(rectangle.y + rectangle.height * 0.5f) / tileSize);
		polygon.setAsBox(rectangle.width * 0.5f / tileSize,
			rectangle.height * 0.5f / tileSize,
			size,
			0.0f);
		return polygon;
	}
	
	private CircleShape getCircle(CircleMapObject circleObject)
	{
		Circle circle = circleObject.getCircle();
		CircleShape circleShape = new CircleShape();
		circleShape.setRadius(circle.radius / tileSize);
		circleShape.setPosition(new Vector2(circle.x / tileSize, circle.y / tileSize));
		return circleShape;
	}
	
	private PolygonShape getPolygon(PolygonMapObject polygonObject)
	{
		PolygonShape polygon = new PolygonShape();
		float[] vertices = polygonObject.getPolygon().getTransformedVertices();
		
		float[] worldVertices = new float[vertices.length];
		
		for (int i = 0; i < vertices.length; ++i)
		{
			worldVertices[i] = vertices[i] / tileSize;
		}
		
		polygon.set(worldVertices);
		return polygon;
	}
	
	private ChainShape getPolyline(PolylineMapObject polylineObject)
	{
		float[] vertices = polylineObject.getPolyline().getTransformedVertices();
		Vector2[] worldVertices = new Vector2[vertices.length / 2];
		
		for (int i = 0; i < vertices.length / 2; ++i)
		{
			worldVertices[i] = new Vector2();
			worldVertices[i].x = vertices[i * 2] / tileSize;
			worldVertices[i].y = vertices[i * 2 + 1] / tileSize;
		}
		
		ChainShape chain = new ChainShape();
		chain.createChain(worldVertices);
		return chain;
	}
}
