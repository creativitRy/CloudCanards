package com.cloudcanards.graphics;

import com.cloudcanards.util.ArrayUtil;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import java.util.Comparator;

/**
 * Renders stuff in z order
 *
 * @author creativitRy
 */
public class RenderableManager implements Renderable
{
	private Array<Renderable> renderables;
	private final RenderableComparator comparator;
	
	private Array<Renderable> tempAdd;
	private Array<Renderable> tempRemove;
	private Array<Renderable> tempChange;
	
	public RenderableManager()
	{
		renderables = new Array<>();
		comparator = new RenderableComparator();
		
		tempAdd = new Array<>();
		tempRemove = new Array<>();
		tempChange = new Array<>();
	}
	
	public RenderableManager(Renderable... renderables)
	{
		this();
		this.renderables.addAll(renderables);
		sort();
	}
	
	private void sort()
	{
		renderables.sort(comparator);
	}
	
	public void add(Renderable renderable)
	{
		tempAdd.add(renderable);
	}
	
	public void remove(Renderable renderable)
	{
		tempRemove.add(renderable);
	}
	
	/**
	 * Call this method when a Renderable's z order is changed
	 *
	 * @param renderable renderable
	 */
	public void markZOrderChanged(Renderable renderable)
	{
		tempChange.add(renderable);
	}
	
	public void clear()
	{
		tempAdd.clear();
		tempRemove.clear();
		tempChange.clear();
		renderables.clear();
	}
	
	@Override
	public void render(SpriteBatch batch, float delta)
	{
		if (tempAdd.size > 0)
		{
			for (Renderable renderable : tempAdd)
			{
				addTemp(renderable);
			}
			tempAdd.clear();
		}
		if (tempRemove.size > 0)
		{
			for (Renderable renderable : tempRemove)
			{
				removeTemp(renderable);
			}
			tempAdd.clear();
		}
		if (tempChange.size > 0)
		{
			for (Renderable renderable : tempChange)
			{
				removeTemp(renderable);
				addTemp(renderable);
			}
			tempAdd.clear();
		}
		
		batch.begin();
		for (Renderable renderable : renderables)
		{
			renderable.render(batch, delta);
		}
		batch.end();
	}
	
	private void addTemp(Renderable renderable)
	{
		int index = ArrayUtil.binarySearch(renderables, renderable, comparator);
		if (index < 0)
			index = -index - 1;
		
		//find last place
		for (; index < renderables.size; index++)
		{
			if (renderables.get(index).getZOrder() != renderable.getZOrder())
				break;
		}
		
		renderables.insert(index, renderable);
	}
	
	/**
	 * Using == not equals()
	 *
	 * @param renderable key to remove
	 */
	private void removeTemp(Renderable renderable)
	{
		renderables.removeValue(renderable, true);
	}
	
	private static class RenderableComparator implements Comparator<Renderable>
	{
		@Override
		public int compare(Renderable o1, Renderable o2)
		{
			return o1.getZOrder() - o2.getZOrder();
		}
	}
}
