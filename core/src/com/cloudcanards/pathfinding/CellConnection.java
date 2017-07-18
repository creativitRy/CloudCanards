package com.cloudcanards.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.Graph;

/**
 * A connection between two nodes of the {@link Graph}. The connection has a non-negative cost that often represents
 * time or
 * distance. However, the cost can be anything you want, for instance a combination of time, distance, and other
 * factors.
 *
 * @author creativitRy
 */
public class CellConnection implements Connection<Cell>
{
	private int cost;
	private Cell fromCell;
	private Cell toCell;
	
	@Override
	public float getCost()
	{
		return cost;
	}
	
	@Override
	public Cell getFromNode()
	{
		return fromCell;
	}
	
	@Override
	public Cell getToNode()
	{
		return toCell;
	}
}
