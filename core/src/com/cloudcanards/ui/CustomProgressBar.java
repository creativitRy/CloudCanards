package com.cloudcanards.ui;


import com.cloudcanards.assets.Assets;
import com.cloudcanards.loading.AbstractLoadAssetTask;
import com.cloudcanards.loading.Loadable;
import com.cloudcanards.loading.ResourceManager;
import com.cloudcanards.loading.ShaderParameter;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * The progressbar image's alpha channel determines how much of the progressbar is drawn
 *
 * @author creativitRy
 */
public class CustomProgressBar extends Actor implements Loadable
{
	private Image progressBar;
	private Image background;
	
	private ShaderProgram shader;
	
	/**
	 * The progressbar image's alpha channel determines how much of the progressbar is drawn
	 */
	public CustomProgressBar(Image progressBar)
	{
		this(progressBar, null);
	}
	
	/**
	 * The progressbar image's alpha channel determines how much of the progressbar is drawn
	 */
	public CustomProgressBar(Image progressBar, Image background)
	{
		this.progressBar = progressBar;
		this.background = background;
	}
	
	@Override
	public void load(ResourceManager resourceManager)
	{
		resourceManager.addTask(new AbstractLoadAssetTask()
		{
			@Override
			public void run()
			{
				resourceManager.getAssetManager().load(Assets.DIR + Assets.TIMER_SHADER, ShaderProgram.class, new ShaderParameter());
			}
			
			@Override
			public void postRun()
			{
				shader = resourceManager.getAssetManager().get(Assets.DIR + Assets.TIMER_SHADER);
			}
		});
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha)
	{
		background.draw(batch, parentAlpha);
		
		batch.setShader(shader);
		
		progressBar.draw(batch, parentAlpha);
		
		//todo: if using shaders, replace back to the shader being used
		batch.setShader(null);
	}
	
	public Image getProgressBar()
	{
		return progressBar;
	}
	
	public Image getBackground()
	{
		return background;
	}
}