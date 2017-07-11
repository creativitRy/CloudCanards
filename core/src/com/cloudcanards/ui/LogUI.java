package com.cloudcanards.ui;

import com.cloudcanards.CloudCanards;
import com.cloudcanards.behavior.Updateable;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Queue;
import com.badlogic.gdx.utils.SnapshotArray;

import java.util.Iterator;

/**
 * LogUI
 *
 * @author creativitRy
 */
public class LogUI extends Table
{
	private static final int DEFAULT_MAX_MESSAGES = 1;
	private static final float MAX_TIME = 5f;
	
	private int maxMessages;
	
	private Queue<Pair> queue;
	private Array<ChangePoster> posters;
	
	private int alignment;
	
	public LogUI(int alignment)
	{
		this(alignment, DEFAULT_MAX_MESSAGES);
	}
	
	public LogUI(int alignment, int maxMessages)
	{
		super(CloudCanards.getInstance().getSkin());
		
		this.maxMessages = maxMessages;
		queue = new Queue<>(maxMessages);
		posters = new Array<>();
		
		setFillParent(true);
		pad(25f);
		
		this.alignment = alignment;
		align(alignment);
	}
	
	/**
	 * Adds a new change poster
	 *
	 * @param poster poster to add
	 * @return this (for chaining)
	 */
	public LogUI addChangePoster(ChangePoster poster)
	{
		poster.setLogUI(this);
		posters.add(poster);
		
		return this;
	}
	
	/**
	 * Removes a change poster that is == to the parameter
	 *
	 * @param poster poster to check with == (not equals)
	 */
	public void removeChangePoster(ChangePoster poster)
	{
		posters.removeValue(poster, true);
	}
	
	/**
	 * Posts a message to this
	 *
	 * @param message message to post
	 */
	public void post(String message)
	{
		while (queue.size >= maxMessages)
		{
			Pair pair = queue.removeFirst();
			fadeRemove(pair.getLabel());
		}
		add(new Label(message, getSkin(), "small", Color.LIGHT_GRAY));
	}
	
	private void add(Label label)
	{
		row();
		label.setAlignment(alignment);
		super.add(label);
		queue.addLast(new Pair(label));
	}
	
	@Override
	public void act(float delta)
	{
		super.act(delta);
		for (ChangePoster poster : posters)
		{
			poster.update(delta);
		}
		
		//remove labels that finished fading
		SnapshotArray<Actor> children = getChildren();
		if (children.size > 0)
		{
			Actor[] actors = children.begin();
			for (int i = 0; i < actors.length; i++)
			{
				if (actors[i] == null)
				{
					continue;
				}
				if (actors[i].getColor().a <= 0)
				{
					removeActor(actors[i]);
				}
			}
			children.end();
		}
		
		//update times
		for (Iterator<Pair> it = queue.iterator(); it.hasNext(); )
		{
			Pair pair = it.next();
			if (pair.getTime() >= MAX_TIME)
			{
				it.remove();
				fadeRemove(pair.getLabel());
			}
			else
			{
				pair.update(delta);
			}
		}
	}
	
	private void fadeRemove(Label label)
	{
		label.addAction(Actions.fadeOut(0.5f));
	}
	
	/**
	 * Posts a message to the log ui once a change occurs. Changes are detected once every frame
	 */
	public abstract static class ChangePoster implements Updateable
	{
		private LogUI logUI;
		private String last;
		
		void setLogUI(LogUI logUI)
		{
			this.logUI = logUI;
		}
		
		@Override
		public void update(float delta)
		{
			String temp = getValue();
			if (isChanged(temp, last))
			{
				logUI.post(temp);
				last = temp;
			}
		}
		
		/**
		 * Checks whether a change occured
		 *
		 * @param oldValue old value
		 * @param newValue new value
		 * @return true if old value != new value
		 */
		protected boolean isChanged(String oldValue, String newValue)
		{
			return !oldValue.equals(newValue);
		}
		
		/**
		 * Gets the string value of the variable being tracked
		 */
		protected abstract String getValue();
	}
	
	private class Pair implements Updateable
	{
		private Label label;
		private float time;
		
		public Pair(Label label)
		{
			this.label = label;
			this.time = 0;
		}
		
		public void update(float delta)
		{
			time += delta;
		}
		
		public float getTime()
		{
			return time;
		}
		
		public Label getLabel()
		{
			return label;
		}
	}
}
