package com.cloudcanards.commands;

import com.cloudcanards.console.AbstractCommand;
import com.cloudcanards.screens.GameScreen;

import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Field;
import com.badlogic.gdx.utils.reflect.ReflectionException;


/**
 * RenderBox2DCommand
 *
 * @author creativitRy
 */
public class RenderBox2DCommand extends AbstractCommand
{
	@Override
	public void execute(String[] args) throws ArrayIndexOutOfBoundsException
	{
		try
		{
			Field field = ClassReflection.getDeclaredField(GameScreen.class, "box2DDebugRenderer");
			field.setAccessible(true);
			Box2DDebugRenderer renderer = (Box2DDebugRenderer) field.get(GameScreen.getInstance());
			
			if (args.length < 2)
			{
				print(renderer.isDrawBodies() ? "true" : "false");
			}
			else
			{
				boolean change;
				if (args[1].equalsIgnoreCase("true"))
				{
					change = true;
				}
				else if (args[1].equalsIgnoreCase("false"))
				{
					change = false;
				}
				else if (args[1].equalsIgnoreCase("toggle"))
				{
					change = !renderer.isDrawBodies();
				}
				else
				{
					throw new ArrayIndexOutOfBoundsException("Illegal argument");
				}
				
				renderer.setDrawBodies(change);
				renderer.setDrawJoints(change);
				renderer.setDrawInactiveBodies(change);
				renderer.setDrawContacts(change);
			}
			//true, true, false, true, false, true
			//drawBodies, drawJoints, drawAABBs, drawInactiveBodies, drawVelocities, drawContacts
			
		}
		catch (ReflectionException e)
		{
			e.printStackTrace();
			print("Maybe you changed the name of the field of the box2d debug renderer?\n" +
				"Refer to RenderBox2dCommand and GameScreen");
		}
	}
	
	@Override
	public String help(int start, String[] args)
	{
		return "Gets whether to draw the debug box2d world or not\n" +
			"Usage:\n" +
			"renderbox2d //gets whether it is currently drawing the world or not\n" +
			"renderbox2d true\n" +
			"renderbox2d false\n" +
			"renderbox2d toggle";
	}
}
