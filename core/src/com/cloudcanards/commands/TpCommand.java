package com.cloudcanards.commands;

import com.cloudcanards.CloudCanards;
import com.cloudcanards.assets.Assets;
import com.cloudcanards.character.CharacterState;
import com.cloudcanards.console.AbstractCommand;
import com.cloudcanards.screens.GameScreen;
import com.cloudcanards.screens.LoadingScreen;

import com.badlogic.gdx.Gdx;

/**
 * TpCommand
 *
 * @author creativitRy
 */
public class TpCommand extends AbstractCommand
{
	public static final float FLOAT_PRECISION = 0.01f;
	private boolean temp;
	
	@Override
	public void execute(String[] args) throws ArrayIndexOutOfBoundsException
	{
		if (GameScreen.getInstance().getPlayer() == null)
		{
			print("Unable to teleport player");
		}
		else if (GameScreen.getInstance().getPlayer().getState() == CharacterState.GRAPPLE)
		{
			print("Player must not be grappling to be teleported");
		}
		else
		{
			if (args[1].matches("[\\d._~]+"))
			{
				float x = GameScreen.getInstance().getPlayer().getBody().getPosition().x;
				float y = GameScreen.getInstance().getPlayer().getBody().getPosition().y;
				
				if (args[1].charAt(0) == '~')
				{
					if (args[1].length() != 1)
					{
						x += Float.parseFloat(args[1].substring(1));
					}
				}
				else
				{
					x = Float.parseFloat(args[1]);
				}
				
				if (args[2].charAt(0) == '~')
				{
					if (args[2].length() != 1)
					{
						y += Float.parseFloat(args[2].substring(1));
					}
				}
				else
				{
					y = Float.parseFloat(args[2]);
				}
				
				temp = true;
				//check collision
				//				GameScreen.getInstance().getWorld().QueryAABB(fixture ->
				//				{
				//					if (CollisionFilters.check(fixture.getFilterData().maskBits, CollisionFilters.CHARACTER))
				//					{
				//						temp = false;
				//						return false;
				//					}
				//
				//					return true;
				//				}, x - 0.5f + FLOAT_PRECISION, y + FLOAT_PRECISION, x + 0.5f - FLOAT_PRECISION, y + 2f - FLOAT_PRECISION);
				
				if (temp)
				//teleport to coordinate
				{
					GameScreen.getInstance().getPlayer().getBody().setTransform(x, y, 0);
					GameScreen.getInstance().getPlayer().getBody().setLinearVelocity(0, 0);
				}
				else
				{
					print("That position is invalid");
				}
				
			}
			else
			{
				if (Gdx.files.internal(Assets.DIR + Assets.LEVEL_DIR + args[1]).exists())
				{
					//move to new map
					addPostExecutable(() -> CloudCanards.getInstance().setScreen(new LoadingScreen(
						new GameScreen(args[1]))));
				}
				else
				{
					print("Unable to find " + Assets.DIR + Assets.LEVEL_DIR + args[1]
						+ ". Make sure to include .tmx");
				}
			}
		}
	}
	
	@Override
	public String help(int start, String[] args)
	{
		return "Teleports player to either a given coordinate or loads a new map\n" +
			"Usage:\n" +
			"tp x y\n" +
			"tp ~relativeX ~relativeY\n" +
			"tp mapName";
	}
}
