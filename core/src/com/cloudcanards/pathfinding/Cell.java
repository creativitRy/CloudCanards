package com.cloudcanards.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.utils.Array;

/**
 * A cell in the level - used for pathfinding
 *
 * @author creativitRy
 */
public class Cell
{
	private static int xBits;
	private static int xDigits;
	
	private static int yBits;
	private static int yDigits;
	
	//use bit shifts to access x and y and z. (z << (ymaxdigitsinbinary + xdigits)) | (y << xdigits) | x
	private int index;
	private Array<Connection<Cell>> connections;
	
	public int getIndex()
	{
		return index;
	}
	
	public int getX()
	{
		return index & xBits;
	}
	
	public int getY()
	{
		return index >> xDigits & yBits;
	}
	
	public int getZ()
	{
		return index >> xBits >> yBits;
	}
	
	public Array<Connection<Cell>> getConnections()
	{
		return connections;
	}
	
	@Override
	public boolean equals(Object o)
	{
		if (this == o)
		{
			return true;
		}
		if (o == null || getClass() != o.getClass())
		{
			return false;
		}
		
		Cell cell = (Cell) o;
		
		return index == cell.index;
	}
	
	@Override
	public int hashCode()
	{
		return index;
	}
}
