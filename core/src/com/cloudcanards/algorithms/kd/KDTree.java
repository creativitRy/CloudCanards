package com.cloudcanards.algorithms.kd;

import com.badlogic.gdx.math.Vector2;

/**
 * Efficient data structure for querying static 2 dimentional coordinates for all points within a box
 *
 * @author creativitRy
 */
public abstract class KDTree<E>
{
	private static final boolean DIVIDE_BY_Y_FIRST = false;
	
	private int size;
	private KDNode<E> root;
	
	/**
	 * Constructs an empty kd-tree
	 */
	public KDTree()
	{
		size = 0;
		root = null;
	}
	
	/**
	 * Inserts an element to the tree
	 * @param element element to insert
	 */
	public void insert(E element)
	{
		//true = divide first element by y values first
		root = insert(element, root, DIVIDE_BY_Y_FIRST);
		size++;
	}
	
	/**
	 * Inserts an element to the tree
	 *
	 * @param element element to insert
	 * @param node    current node to look at
	 * @param useY    whether to split by y values (true) or x values (false)
	 * @return newly created node or a node with its child replaced
	 */
	private KDNode<E> insert(E element, KDNode<E> node, boolean useY)
	{
		if (node == null)
			return new KDNode<>(element, getPosition(element), null, null, useY);
			//todo: remove this else if before release
		else if (element == node.getElement())
			throw new IllegalArgumentException("Duplicates (based on ==) not allowed");
		else if (compare(element, node.getElement(), useY))
			node.setLeft(insert(element, node.getLeft(), !useY));
		else
			node.setRight(insert(element, node.getRight(), !useY));
		return node;
	}
	
	/**
	 * Gets the element with the smallest x coordinate
	 *
	 * @return element with the smallest x coordinate
	 */
	public E getMinX()
	{
		return getMin(root, false, DIVIDE_BY_Y_FIRST);
	}
	
	/**
	 * Gets the element with the smallest y coordinate
	 *
	 * @return element with the smallest y coordinate
	 */
	public E getMinY()
	{
		return getMin(root, true, DIVIDE_BY_Y_FIRST);
	}
	
	/**
	 * Gets the element with the smallest coordinate
	 *
	 * @param node     current node to check
	 * @param findY    compare by y values if true, x values if false
	 * @param currUseY whether the current node is split by y (true) or x (false)
	 * @return the minimum of find at left, find at right, and current; or null if node is empty
	 */
	private E getMin(KDNode<E> node, boolean findY, boolean currUseY)
	{
		if (node == null)
			return null;
		if (findY == currUseY)
		{
			if (node.getLeft() == null)
				return node.getElement();
			return getMin(node.getLeft(), findY, !currUseY);
		}
		else
		{
			//return the minimum of find at left, find at right, and current
			E min = getMin(node.getLeft(), findY, !currUseY);
			E newMin = getMin(node.getRight(), findY, !currUseY);
			if (min == null)
				min = newMin;
			else if (newMin != null && compare(newMin, min, findY))
				min = newMin;
			newMin = node.getElement();
			if (min == null)
				min = newMin;
			else if (newMin != null && compare(newMin, min, findY))
				min = newMin;
			return min;
		}
	}
	
	/**
	 * Checks < based on x if !useY and y if it useY
	 *
	 * @param e1       element 1
	 * @param e2       element 2
	 * @param useY true to use y to compare
	 * @return element 1 < element 2
	 */
	private boolean compare(E e1, E e2, boolean useY)
	{
		if (useY)
			return getPosition(e1).y < getPosition(e2).y;
		return getPosition(e1).x < getPosition(e2).x;
	}
	
	/**
	 * When given data, gets its position
	 *
	 * @param value
	 * @return
	 */
	public abstract Vector2 getPosition(E value);
	
	public boolean isEmpty()
	{
		return size == 0;
	}
	
	public int size()
	{
		return size;
	}
}
