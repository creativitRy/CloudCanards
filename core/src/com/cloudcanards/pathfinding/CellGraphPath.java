package com.cloudcanards.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.Graph;
import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.utils.Array;

import java.util.Iterator;

/**
 * CellGraphPath represents a path in a {@link Graph}. Note that a path can be defined in terms of nodes or
 * {@link Connection connections} so that multiple edges between the same pair of nodes can be discriminated.
 *
 * @author creativitRy
 */
public class CellGraphPath implements GraphPath<Cell>
{
	private Array<Cell> cells;
	
	@Override
	public int getCount()
	{
		return cells.size;
	}
	
	@Override
	public Cell get(int index)
	{
		return cells.get(index);
	}
	
	@Override
	public void add(Cell cell)
	{
		cells.add(cell);
	}
	
	@Override
	public void clear()
	{
		cells.clear();
	}
	
	@Override
	public void reverse()
	{
		cells.reverse();
	}
	
	@Override
	public Iterator<Cell> iterator()
	{
		return cells.iterator();
	}
}
