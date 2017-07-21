package com.cloudcanards.pathfinding;

import com.badlogic.gdx.ai.msg.MessageDispatcher;
import com.badlogic.gdx.ai.pfa.PathFinderQueue;
import com.badlogic.gdx.ai.pfa.indexed.IndexedAStarPathFinder;

/**
 * PathFindingManager
 *
 * @author creativitRy
 */
public class PathFindingManager
{
	private static final PathFindingManager INSTANCE = new PathFindingManager();
	
	public static PathFindingManager getInstance()
	{
		return INSTANCE;
	}
	
	private final CellGraph graph;
	private final IndexedAStarPathFinder<Cell> pathFinder;
	private final PathFinderQueue<Cell> pathFinderQueue;
	
	private MessageDispatcher messageDispatcher;
	
	private PathFindingManager()
	{
		messageDispatcher = new MessageDispatcher();
		
		graph = new CellGraph();
		pathFinder = new IndexedAStarPathFinder<>(graph, false); //true for debugging
		pathFinderQueue = new PathFinderQueue<>(pathFinder);
	}
	
	public MessageDispatcher getMessageDispatcher()
	{
		return messageDispatcher;
	}
	
	public Cell getCellAtPos(float x, float y)
	{
		return graph.getCellAtPos(Math.round(x), Math.round(y));
	}
	
	public Cell getCellAtPos(float x, float y, float jump)
	{
		return graph.getCellAtPos(Math.round(x), Math.round(y), Math.round(jump));
	}
}
