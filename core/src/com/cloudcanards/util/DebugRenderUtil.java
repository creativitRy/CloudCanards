package com.cloudcanards.util;

import com.cloudcanards.screens.GameScreen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

/**
 * DebugRenderUtil
 *
 * @author creativitRy
 */
public class DebugRenderUtil
{
	private static final DebugRenderUtil INSTANCE = new DebugRenderUtil();
	
	public static DebugRenderUtil getInstance()
	{
		return INSTANCE;
	}
	
	private ShapeRenderer shapeRenderer;
	
	private DebugRenderUtil()
	{
		shapeRenderer = new ShapeRenderer();
	}
	
	public void drawLine(Vector2 start, Vector2 end)
	{
		drawLine(start, end, 2, Color.WHITE);
	}
	
	public void drawLine(Vector2 start, Vector2 end, int width, Color color)
	{
		Gdx.gl.glLineWidth(width);
		setProjectionMatrix();
		shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		shapeRenderer.setColor(color);
		shapeRenderer.line(start, end);
		shapeRenderer.end();
		Gdx.gl.glLineWidth(1);
	}
	
	public void drawCircle(float x, float y, float radius, Color color)
	{
		drawCircle(x, y, radius, color, true);
	}
	
	public void drawCircle(float x, float y, float radius, Color color, boolean fill)
	{
		if (fill)
		{
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		}
		else
		{
			shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
		}
		shapeRenderer.setColor(color);
		shapeRenderer.circle(x, y, radius);
	}
	
	private void setProjectionMatrix()
	{
		shapeRenderer.setProjectionMatrix(GameScreen.getInstance().getCamera().combined);
	}
}
