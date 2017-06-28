package com.cloudcanards.algorithms.kd;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayDeque;
import java.util.Comparator;

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
	 * Constructs an empty k-d tree
	 */
	public KDTree()
	{
		size = 0;
		root = null;
	}
	
	/**
	 * Constructs a balanced tree and calls {@link #init}. O((NlogN)^2) ayy lmao
	 *
	 * @param elements array of elements to insert. Array will be modified
	 */
	public KDTree(Array<E> elements)
	{
		super();
		
		addAll(elements);
		
		init();
	}
	
	/**
	 * Adds all these elements in hopefully a balanced manner. O((NlogN)^2) ayy lmao
	 *
	 * @param elements array of elements to insert. Array will be modified
	 */
	public void addAll(Array<E> elements)
	{
		Comparator<E> horizComp = (o1, o2) ->
		{
			if (getPosition(o1).x == getPosition(o2).x)
				return Float.compare(getPosition(o1).y, getPosition(o2).y);
			return Float.compare(getPosition(o1).x, getPosition(o2).x);
		};
		Comparator<E> vertComp = (o1, o2) ->
		{
			if (getPosition(o1).y == getPosition(o2).y)
				return Float.compare(getPosition(o1).x, getPosition(o2).x);
			return Float.compare(getPosition(o1).y, getPosition(o2).y);
		};
		
		size += elements.size;
		build(elements, horizComp, vertComp, DIVIDE_BY_Y_FIRST);
	}
	
	private void build(Array<E> elements, Comparator<E> horizComp, Comparator<E> vertComp, boolean currUseY)
	{
		if (elements.size == 0)
			return;
		if (currUseY)
			elements.sort(horizComp);
		else
			elements.sort(vertComp);
		
		int mid = elements.size >>> 1; //divide by 2
		
		insert(elements.removeIndex(mid));
		
		Array<E> left = new Array<>(elements);
		Array<E> right = new Array<>();
		while (left.size > mid)
		{
			right.add(left.pop());
		}
		build(left, horizComp, vertComp, !currUseY);
		build(right, horizComp, vertComp, !currUseY);
	}
	
	/**
	 * Inserts an element to the tree
	 *
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
			return new KDNode<>(element, getPosition(element));
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
	 * Calculates the bounds of each node. Call before attempting to use {@link #getAllElementsInBox}
	 */
	public void init()
	{
		init(root,
			new Rectangle(getPosition(getMinX()).x, getPosition(getMinY()).y, getPosition(getMaxX()).x, getPosition(getMaxY()).y),
			DIVIDE_BY_Y_FIRST);
	}
	
	private void init(KDNode<E> node, Rectangle bound, boolean currUseY)
	{
		if (node == null)
			return;
		node.setBounds(bound);
		init(node.getLeft(), bound.divide(node.getPosition(), true, currUseY), !currUseY);
		init(node.getRight(), bound.divide(node.getPosition(), false, currUseY), !currUseY);
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
	 * Gets the element with the largest x coordinate
	 *
	 * @return element with the largest x coordinate
	 */
	public E getMaxX()
	{
		return getMax(root, false, DIVIDE_BY_Y_FIRST);
	}
	
	/**
	 * Gets the element with the largest y coordinate
	 *
	 * @return element with the largest y coordinate
	 */
	public E getMaxY()
	{
		return getMax(root, true, DIVIDE_BY_Y_FIRST);
	}
	
	/**
	 * Gets the element with the largest coordinate
	 *
	 * @param node     current node to check
	 * @param findY    compare by y values if true, x values if false
	 * @param currUseY whether the current node is split by y (true) or x (false)
	 * @return the maximum of find at left, find at right, and current; or null if node is empty
	 */
	private E getMax(KDNode<E> node, boolean findY, boolean currUseY)
	{
		if (node == null)
			return null;
		if (findY == currUseY)
		{
			if (node.getRight() == null)
				return node.getElement();
			return getMax(node.getRight(), findY, !currUseY);
		}
		else
		{
			//return the maximum of find at left, find at right, and current
			E max = getMax(node.getLeft(), findY, !currUseY);
			E newMax = getMax(node.getRight(), findY, !currUseY);
			if (max == null)
				max = newMax;
			else if (newMax != null && compare(max, newMax, findY))
				max = newMax;
			newMax = node.getElement();
			if (max == null)
				max = newMax;
			else if (newMax != null && compare(max, newMax, findY))
				max = newMax;
			return max;
		}
	}
	
	/**
	 * Checks whether the tree contains the element
	 *
	 * @param element element to find
	 * @return true if element is found
	 */
	public boolean contains(E element)
	{
		return element != null && contains(element, root, DIVIDE_BY_Y_FIRST);
	}
	
	/**
	 * Checks whether the tree contains the element
	 *
	 * @param element  element to find
	 * @param node     current node being checked
	 * @param currUseY whether the current node is split by y (true) or x (false)
	 * @return true if element is found
	 */
	private boolean contains(E element, KDNode<E> node, boolean currUseY)
	{
		if (node == null)
			return false;
		if (node.getElement() == element)
			return true;
		if (compare(element, node.getElement(), currUseY))
			return contains(element, node.getLeft(), !currUseY);
		return contains(element, node.getRight(), !currUseY);
	}
	
	/**
	 * Checks < based on x if !useY and y if it useY
	 *
	 * @param e1   element 1
	 * @param e2   element 2
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
	 * Within the rectangle, gets all the elements
	 *
	 * @param array array to fill with elements within rectangle. Array is cleared
	 * @param x1    min x inclusive
	 * @param y1    min y inclusive
	 * @param x2    max x inclusive
	 * @param y2    max y inclusive
	 */
	public void getAllElementsInBox(Array<E> array, float x1, float y1, float x2, float y2)
	{
		array.clear();
		getAllElementsInBox(array, root, new Rectangle(x1, y1, x2, y2), DIVIDE_BY_Y_FIRST);
	}
	
	/**
	 * Within the rectangle, gets all the elements. Call {@link #init} first
	 *
	 * @param array    array to fill with elements within rectangle
	 * @param node     current node being checked
	 * @param range    x1, y1, x2, y2
	 * @param currUseY whether the current node is split by y (true) or x (false)
	 */
	private void getAllElementsInBox(Array<E> array, KDNode<E> node, Rectangle range, boolean currUseY)
	{
		if (node == null)
			return;
		
		//if entire subtree is within range
		if (range.contains(node.getBounds()))
			reportTree(array, node);
		else
		{
			if (range.isWithinRange(node.getPosition()))
			{
				array.add(node.getElement());
			}
			if (range.intersects(node.getBounds()))
			{
				getAllElementsInBox(array, node.getLeft(), range, !currUseY);
				getAllElementsInBox(array, node.getRight(), range, !currUseY);
			}
		}
	}
	
	/**
	 * Puts every node and its children into the array
	 *
	 * @param array array to put nodes into
	 * @param node  current node being checked
	 */
	private void reportTree(Array<E> array, KDNode<E> node)
	{
		if (node == null)
			return;
		array.add(node.getElement());
		reportTree(array, node.getLeft());
		reportTree(array, node.getRight());
	}
	
	/**
	 * When given data, gets its position
	 *
	 * @param element element to get position from
	 * @return position of element
	 */
	protected abstract Vector2 getPosition(E element);
	
	public boolean isEmpty()
	{
		return size == 0;
	}
	
	public int size()
	{
		return size;
	}
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		
		ArrayDeque<KDNode<E>> deque = new ArrayDeque<>();
		deque.add(root);
		
		while (!deque.isEmpty())
		{
			KDNode<E> node = deque.remove();
			sb.append(node).append(" ");
			
			if (node.getLeft() != null)
				deque.add(node.getLeft());
			if (node.getRight() != null)
				deque.add(node.getRight());
		}
		
		return sb.toString();
	}
}
