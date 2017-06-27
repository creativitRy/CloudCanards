package com.cloudcanards.algorithms.kd;

import com.badlogic.gdx.math.Vector2;

/**
 * KDNode
 *
 * @author creativitRy
 */
class KDNode<E>
{
	private KDNode<E> left;
	private KDNode<E> right;
	
	private final Vector2 position;
	private Rectangle bounds;
	
	private final E element;
	
	public KDNode(E element, Vector2 position)
	{
		this.element = element;
		this.position = position;
	}
	
	public Vector2 getPosition()
	{
		return position;
	}
	
	public E getElement()
	{
		return element;
	}
	
	public KDNode<E> getLeft()
	{
		return left;
	}
	
	public void setLeft(KDNode<E> left)
	{
		this.left = left;
	}
	
	public KDNode<E> getRight()
	{
		return right;
	}
	
	public void setRight(KDNode<E> right)
	{
		this.right = right;
	}
	
	public Rectangle getBounds()
	{
		return bounds;
	}
	
	public void setBounds(Rectangle bounds)
	{
		this.bounds = bounds;
	}
	
	@Override
	public String toString()
	{
		return "Node{" +
			element +
			", bounds=" + bounds +
			'}';
	}
}
