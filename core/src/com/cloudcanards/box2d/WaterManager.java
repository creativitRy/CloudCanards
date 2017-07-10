package com.cloudcanards.box2d;

import com.cloudcanards.behavior.Updateable;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.Pool;

import java.util.Iterator;

/**
 * WaterManager - for buoyant objects
 * <p>
 * http://www.iforce2d.net/b2dtut/buoyancy
 * todo URGENT: check for pooling errors - did I forget to free stuff? did I free stuff that are used elsewhere?
 *
 * @author creativitRy
 */
public class WaterManager implements Updateable
{
	private static final int NUM_VERTICES_IN_CIRCLE_PER_RADIUS = 6; //12 per tile
	private static final int MAX_CIRCLE_VERTICES = 128;
	
	private static final float DRAG_MODIFIER = 0.25f;
	private static final float LIFT_MODIFIER = 0.25f;
	private static final float MAX_DRAG = 2000;
	private static final float MAX_LIFT = 500;
	
	private World world;
	
	private ObjectMap<Fixture, ObjectSet<Fixture>> waterGroups;
	private ObjectMap<Fixture, Array<Vector2>> waterVertices;
	
	private Pool<Array<Vector2>> vectorArrayPool;
	private Pool<Vector2> vectorPool;
	
	
	public WaterManager(World world)
	{
		this.world = world;
		
		waterGroups = new ObjectMap<>();
		waterVertices = new ObjectMap<>();
		
		vectorArrayPool = new Pool<Array<Vector2>>()
		{
			@Override
			protected Array<Vector2> newObject()
			{
				return new Array<>();
			}
			
			@Override
			protected void reset(Array<Vector2> object)
			{
				//free all of its contents as well
				for (Iterator<Vector2> it = object.iterator(); it.hasNext(); )
				{
					Vector2 next = it.next();
					vectorPool.free(next);
					it.remove();
				}
			}
		};
		vectorPool = new Pool<Vector2>()
		{
			@Override
			protected Vector2 newObject()
			{
				return new Vector2();
			}
			
			@Override
			protected void reset(Vector2 object)
			{
				//does nothing
			}
		};
	}
	
	public Fixture createWater(Body body, FixtureDef fixtureDef, float density)
	{
		fixtureDef.density = density;
		fixtureDef.isSensor = true;
		
		Fixture fixture = body.createFixture(fixtureDef);
		fixtureDef.shape.dispose(); //?
		fixture.setUserData(new AdvancedCollidable()
		{
			@Override
			public void onBeginContact(Fixture contactedFixture)
			{
				if (contactedFixture.getBody().getType() == BodyDef.BodyType.DynamicBody &&
					(contactedFixture.getShape() instanceof PolygonShape || contactedFixture.getShape() instanceof CircleShape))
				{
					waterGroups.get(fixture).add(contactedFixture);
				}
			}
			
			@Override
			public void onEndContact(Fixture contactedFixture)
			{
				if (contactedFixture.getBody().getType() == BodyDef.BodyType.DynamicBody)
				{
					waterGroups.get(fixture).remove(contactedFixture);
				}
			}
		});
		registerWater(fixture);
		return fixture;
	}
	
	@Override
	public void update(float delta)
	{
		for (ObjectMap.Entry<Fixture, ObjectSet<Fixture>> waterGroup : waterGroups)
		{
			Fixture water = waterGroup.key;
			for (Fixture fixture : waterGroup.value)
			{
				//get intersection points
				Array<Vector2> intersectionVertices = getIntersection(water, fixture);
				
				//if any
				if (intersectionVertices.size > 0)
				{
					//compute its centroid and area
					//final int count = intersectionVertices.size; //this should be >= 3
					
					Vector2 centroid = vectorPool.obtain().set(0, 0);
					float area = 0f;
					
					Vector2 temp = vectorPool.obtain();
					
					for (int i = 0; i < intersectionVertices.size; i++)
					{
						//triangle vertices
						Vector2 p1 = intersectionVertices.get(i);
						Vector2 p2 = intersectionVertices.get(i + 1 < intersectionVertices.size ? i + 1 : 0); //get next vertex
						
						float cross = p1.crs(p2);
						
						float triangleArea = cross / 2f;
						area += triangleArea;
						
						centroid.add(temp.set(p1).add(p2).scl(triangleArea / 3f));
					}
					
					if (area > 0)
					{
						centroid.scl(1f / area);
					}
					else
					{
						area = 0;
					}
					
					float displacedMass = water.getDensity() * area;
					fixture.getBody().applyForce(
						displacedMass * -world.getGravity().x, displacedMass * -world.getGravity().y,
						centroid.x, centroid.y, true);
					
					Vector2 normal = vectorPool.obtain();
					
					//drag
					for (int i = 0; i < intersectionVertices.size; i++)
					{
						Vector2 v0 = intersectionVertices.get(i);
						Vector2 v1 = intersectionVertices.get(i + 1 < intersectionVertices.size ? i + 1 : 0); //get next vertex
						Vector2 mid = vectorPool.obtain().set(v0).add(v1).scl(0.5f);
						
						Vector2 velDir = fixture.getBody().getLinearVelocityFromWorldPoint(mid).sub(
							water.getBody().getLinearVelocityFromWorldPoint(mid));
						float velLen = velDir.len();
						if (velLen != 0)
						{
							velDir.scl(1 / velLen); //normalize
						}
						
						temp.set(v1).sub(v0);
						float edgeLen = temp.len();
						if (edgeLen != 0)
						{
							temp.scl(1 / edgeLen); //normalize
						}
						normal.set(temp.y, -temp.x);
						float dragDot = normal.dot(velDir);
						if (dragDot < 0)
						{
							continue; //normal points backwards
						}
						
						//apply drag
						float dragMag = Math.min(MAX_DRAG,
							dragDot * DRAG_MODIFIER * edgeLen * water.getDensity() * velLen * velLen);
						fixture.getBody().applyForce(dragMag * -velDir.x, dragMag * -velDir.y,
							mid.x, mid.y, true);
						
						//apply lift
						float liftDot = temp.dot(velDir);
						float liftMag = Math.min(MAX_LIFT,
							dragDot * liftDot * LIFT_MODIFIER * edgeLen * water.getDensity() * velLen * velLen);
						Vector2 liftDir = normal.set(-velDir.y, velDir.x);
						fixture.getBody().applyForce(liftMag * liftDir.x, liftMag * liftDir.y,
							mid.x, mid.y, true);
					}
					
					vectorPool.free(temp);
					vectorPool.free(normal);
				}
				
			}
		}
	}
	
	/**
	 * Uses Sutherland-Hodgeman polygon clipping algorithm.
	 * <p>
	 * http://rosettacode.org/wiki/Sutherland-Hodgman_polygon_clipping
	 * <p>
	 * todo: if water is only a flat surface, you can optimize this to be way faster
	 *
	 * @param water   water
	 * @param fixture colliding fixture. NOTE: THIS SHAPE NEEDS TO BE CONVEX TO WORK
	 * @return clipped points
	 */
	private Array<Vector2> getIntersection(Fixture water, Fixture fixture)
	{
		Array<Vector2> subject = waterVertices.get(water);
		Array<Vector2> clipper;
		if (fixture.getShape() instanceof PolygonShape)
		{
			clipper = getVertices(fixture.getBody(), (PolygonShape) fixture.getShape());
		}
		else if (fixture.getShape() instanceof CircleShape)
		{
			clipper = getVertices(fixture.getBody(), (CircleShape) fixture.getShape());
		}
		else
		{
			throw new RuntimeException("Fixture colliding with water can only be a polygon or a circle");
		}
		Array<Vector2> result = vectorArrayPool.obtain();
		result.addAll(subject);
		
		result = clipPolygon(subject, clipper, result);
		
		vectorArrayPool.free(clipper);
		
		return result;
	}
	
	private Array<Vector2> clipPolygon(Array<Vector2> subject, Array<Vector2> clipper, Array<Vector2> result)
	{
		int len = clipper.size;
		for (int i = 0; i < len; i++)
		{
			int len2 = result.size;
			Array<Vector2> input = result;
			result = vectorArrayPool.obtain();
			
			Vector2 a = clipper.get((i + len - 1) % len);
			Vector2 b = clipper.get(i);
			
			for (int j = 0; j < len2; j++)
			{
				Vector2 p = input.get((j + len2 - 1) % len2);
				Vector2 q = input.get(j);
				
				if (isInside(a, b, q))
				{
					if (!isInside(a, b, p))
					{
						result.add(intersectionOf(a, b, p, q));
					}
					result.add(q);
				}
				else if (isInside(a, b, p))
				{
					result.add(intersectionOf(a, b, p, q));
				}
			}
		}
		
		return result;
	}
	
	private boolean isInside(Vector2 a, Vector2 b, Vector2 c)
	{
		return (a.x - c.x) * (b.y - c.y) > (a.y - c.y) * (b.x - c.x);
	}
	
	private Vector2 intersectionOf(Vector2 a, Vector2 b, Vector2 p, Vector2 q)
	{
		float a1 = b.y - a.y;
		float b1 = a.x - b.x;
		float c1 = a1 * a.x + b1 * a.y;
		
		float a2 = q.y - p.y;
		float b2 = p.x - q.x;
		float c2 = a2 * p.x + b2 * p.y;
		
		float det = a1 * b2 - a2 * b1;
		
		return vectorPool.obtain().set(
			(b2 * c1 - b1 * c2) / det,
			(a1 * c2 - a2 * c1) / det);
	}
	
	private Array<Vector2> getVertices(Body body, PolygonShape polygon)
	{
		Array<Vector2> array = vectorArrayPool.obtain();
		for (int i = 0; i < polygon.getVertexCount(); i++)
		{
			Vector2 vertex = vectorPool.obtain();
			polygon.getVertex(i, vertex);
			array.add(getWorldPoint(body, vertex));
		}
		
		return array;
	}
	
	private Array<Vector2> getVertices(Body body, CircleShape circle)
	{
		Array<Vector2> array = vectorArrayPool.obtain();
		int numVertices = Math.max(3, Math.min(MAX_CIRCLE_VERTICES,
			(int) (circle.getRadius() * NUM_VERTICES_IN_CIRCLE_PER_RADIUS)));
		final float deltaAngle = MathUtils.PI2 / numVertices;
		for (int i = 0; i < numVertices; i++)
		{
			float angle = i * deltaAngle;
			array.add(getWorldPoint(body, vectorPool.obtain().set(
				MathUtils.cos(angle) * circle.getRadius(), MathUtils.sin(angle) * circle.getRadius())));
		}
		
		return array;
	}
	
	/**
	 * Get the world coordinates of a point given the local coordinates.
	 * Note that the localPoint Vector2 instance is modified and returned.
	 *
	 * @param body       body to calculate world point from
	 * @param localPoint a point on the body measured relative the the body's origin.
	 * @return the same point expressed in world coordinates.
	 */
	private Vector2 getWorldPoint(Body body, Vector2 localPoint)
	{
		return localPoint.set(body.getWorldPoint(localPoint));
	}
	
	private void registerWater(Fixture fixture)
	{
		waterGroups.put(fixture, new ObjectSet<>());
		
		if (fixture.getShape() instanceof PolygonShape)
		{
			waterVertices.put(fixture, getVertices(fixture.getBody(), (PolygonShape) fixture.getShape()));
		}
		else if (fixture.getShape() instanceof CircleShape)
		{
			waterVertices.put(fixture, getVertices(fixture.getBody(), (CircleShape) fixture.getShape()));
		}
		else
		{
			throw new RuntimeException("Water can only be a polygon or a circle");
		}
	}
	
	/**
	 * I don't see any reason why water might be destroyed. If I'm wrong, call this method
	 *
	 * @param fixture water fixture to unregister
	 */
	public void unregisterWater(Fixture fixture)
	{
		waterGroups.remove(fixture);
		vectorArrayPool.free(waterVertices.remove(fixture));
	}
}
