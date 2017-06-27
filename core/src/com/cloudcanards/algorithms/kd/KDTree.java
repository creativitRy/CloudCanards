package com.cloudcanards.algorithms.kd;

import com.badlogic.gdx.math.Vector2;

/**
 * Efficient data structure for querying static 2 dimentional coordinates for all points within a box
 *
 * @author creativitRy
 */
public abstract class KDTree<E>
{
	private int size;
	private KDNode<E> root;
	
	public KDTree()
	{
		size = 0;
		root = null;
	}
	
	public void insert(E element)
	{
		root = insert(element, root, true);
	}
	
	private KDNode<E> insert(E element, KDNode<E> node, boolean vertical)
	{
		if (node == null)
			return new KDNode<>(element, getPosition(element), null, null, vertical);
			//todo: remove this else if before release
		else if (element == node.getElement())
			throw new IllegalArgumentException("Duplicates (based on ==) not allowed");
		else if (compare(element, node.getElement(), vertical))
			node.setLeft(insert(element, node.getLeft(), !vertical));
		else
			node.setRight(insert(element, node.getRight(), !vertical));
		return node;
	}
	
	/**
	 * Checks < based on x if it is horiz and y if it is vert
	 *
	 * @param e1       element 1
	 * @param e2       element 2
	 * @param vertical true to use y to compare
	 * @return element 1 < element 2
	 */
	private boolean compare(E e1, E e2, boolean vertical)
	{
		if (vertical)
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
}
