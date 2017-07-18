package com.cloudcanards.pathfinding;

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.ai.pfa.indexed.IndexedGraph;
import com.badlogic.gdx.utils.Array;

/**
 * CellGraph is a collection of nodes, each one having a collection of outgoing {@link Connection connections}.
 *
 * @author creativitRy
 */
public class CellGraph implements IndexedGraph<Cell>
{
	@Override
	public int getIndex(Cell cell)
	{
		return cell.getIndex();
	}
	
	@Override
	public int getNodeCount()
	{
		return 0;
	}
	
	@Override
	public Array<Connection<Cell>> getConnections(Cell fromCell)
	{
		return fromCell.getConnections();
	}
}
