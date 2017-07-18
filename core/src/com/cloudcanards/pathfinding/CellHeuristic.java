package com.cloudcanards.pathfinding;

import com.badlogic.gdx.ai.pfa.Heuristic;

/**
 * CellHeuristic - uses manhattan
 * <p>
 * A {@code Heuristic} generates estimates of the cost to move from a given node to the goal.
 * <p>
 * With a heuristic function pathfinding algorithms can choose the node that is most likely to lead to the optimal path.
 * The
 * notion of "most likely" is controlled by a heuristic. If the heuristic is accurate, then the algorithm will be
 * efficient. If
 * the heuristic is terrible, then it can perform even worse than other algorithms that don't use any heuristic function
 * such as
 * Dijkstra.
 *
 * @author creativitRy
 */
public class CellHeuristic implements Heuristic<Cell>
{
	@Override
	public float estimate(Cell node, Cell endNode)
	{
		return manhattan(node.getX() - endNode.getY(), node.getY() - endNode.getY());
	}
	
	private float euclidean2(float x, float y)
	{
		return x * x + y * y;
	}
	
	private float manhattan(float x, float y)
	{
		return Math.abs(x) + Math.abs(y);
	}
}
