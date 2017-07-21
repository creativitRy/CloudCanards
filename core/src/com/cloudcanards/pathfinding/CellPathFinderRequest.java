package com.cloudcanards.pathfinding;

import com.badlogic.gdx.ai.pfa.GraphPath;
import com.badlogic.gdx.ai.pfa.PathFinderRequest;

/**
 * CellPathFinderRequest
 *
 * @author creativitRy
 */
public class CellPathFinderRequest extends PathFinderRequest<Cell>
{
	public CellPathFinderRequest(float startX, float startY, float endX, float endY, CellGraphPath resultPath)
	{
		this(PathFindingManager.getInstance().getCellAtPos(startX, startY),
			PathFindingManager.getInstance().getCellAtPos(startX, startY),
			new CellHeuristic(),
			resultPath);
	}
	
	public CellPathFinderRequest(float startX, float startY, float startJump, float endX, float endY, CellGraphPath resultPath)
	{
		this(PathFindingManager.getInstance().getCellAtPos(startX, startY, startJump),
			PathFindingManager.getInstance().getCellAtPos(startX, startY),
			new CellHeuristic(),
			resultPath);
	}
	
	private CellPathFinderRequest(Cell startNode, Cell endNode, CellHeuristic heuristic, GraphPath<Cell> resultPath)
	{
		super(startNode, endNode, heuristic, resultPath, PathFindingManager.getInstance().getMessageDispatcher());
	}
	
	public boolean isPathFound()
	{
		return pathFound;
	}
}
