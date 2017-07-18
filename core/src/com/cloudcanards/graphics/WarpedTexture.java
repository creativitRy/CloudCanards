package com.cloudcanards.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Texture that can be warped based on a grid of points. Useful for cloth simulations.
 * <p>
 * To use, just construct a new instance and render. To change grid coordinates, call {@link #getGrid()}
 *
 * @author creativitRy
 */
public class WarpedTexture implements Renderable
{
	private static final int X_VERTEX = 0;
	private static final int Y_VERTEX = 1;
	private static final int COLOR_VERTEX = 2;
	private static final int U_VERTEX = 3;
	private static final int V_VERTEX = 4;
	private static final int NUM_ELEMENTS_IN_VERTEX = 5;
	private static final int NUM_VERTICES_PER_SQUARE = 4;
	
	private Texture texture;
	
	/**
	 * U in texture coordinates
	 */
	private float uMin;
	/**
	 * V in texture coordinates
	 */
	private float vMin;
	/**
	 * U2 - U in texture coordinates
	 */
	private float uLen;
	/**
	 * V2 - V in texture coordinates
	 */
	private float vLen;
	
	/*
	 * UV information:
	 *
	 * Rectangle of coordinates that are within (0,0) and (1,1) inclusive.
	 *
	 * (0, 0) is the top left of the texture and (1,1) is the bottom right of the texture.
	 *
	 * When a coordinate is out of range, refer to libgdx/opengl texture wrapping
	 *
	 * Other points will be lineary interpolated
	 */
	
	/**
	 * World coordinates of individual points of the texture - representing a grid pattern
	 * <p>
	 * [height][width] where [0][0] is bottom left and [height-1][width-1] is top right in world coordinate
	 * <p>
	 * Each coordinate will map to a uv coordinate based on its array index
	 */
	private Vector2[][] grid;
	
	/**
	 * x, y, color, u, v
	 *
	 * @see SpriteBatch#draw(Texture, float[], int, int)
	 */
	private float[] spriteVertices;
	
	/**
	 * What to subtract from top right box's bottom left vertex to go to bottom right box's top left vertex
	 */
	private int verticalSeparation;
	
	/**
	 * @param texture    texture
	 * @param x          x position of the bottom left corner of the grid
	 * @param y          y position of the bottom left corner of the grid
	 * @param width      width of texture
	 * @param height     height of texture
	 * @param gridWidth  width of grid where 3 would have 3 vertical lines
	 * @param gridHeight height of grid where 3 would have 3 horizontal lines
	 */
	public WarpedTexture(Texture texture, float x, float y, float width, float height, int gridWidth, int gridHeight)
	{
		initTexture(texture);
		fillGrid(x, y, width, height, gridWidth, gridHeight);
		initSpriteVertices();
	}
	
	/**
	 * @param texture texture
	 * @param grid    rectangular array of World coordinates of individual points of the texture - representing a
	 *                grid pattern
	 *                <p>
	 *                [height][width] where [0][0] is bottom left and [height-1][width-1] is top right in world
	 *                coordinate
	 *                <p>
	 *                Each coordinate will map to a uv coordinate based on its array index
	 */
	public WarpedTexture(Texture texture, Vector2[][] grid)
	{
		initTexture(texture);
		this.grid = grid;
		initSpriteVertices();
	}
	
	/**
	 * @param textureRegion texture
	 * @param x             x position of the bottom left corner of the grid
	 * @param y             y position of the bottom left corner of the grid
	 * @param width         width of texture
	 * @param height        height of texture
	 * @param gridWidth     width of grid where 3 would have 3 vertical lines
	 * @param gridHeight    height of grid where 3 would have 3 horizontal lines
	 */
	public WarpedTexture(TextureRegion textureRegion, float x, float y, float width, float height, int gridWidth, int gridHeight)
	{
		initTexture(textureRegion);
		fillGrid(x, y, width, height, gridWidth, gridHeight);
		initSpriteVertices();
	}
	
	/**
	 * @param textureRegion texture
	 * @param grid          rectangular array of World coordinates of individual points of the texture - representing a
	 *                      grid pattern
	 *                      <p>
	 *                      [height][width] where [0][0] is bottom left and [height-1][width-1] is top right in world
	 *                      coordinate
	 *                      <p>
	 *                      Each coordinate will map to a uv coordinate based on its array index
	 */
	public WarpedTexture(TextureRegion textureRegion, Vector2[][] grid)
	{
		initTexture(textureRegion);
		this.grid = grid;
		initSpriteVertices();
	}
	
	
	/**
	 * Inits texture and uv coords
	 */
	private void initTexture(Texture texture)
	{
		this.texture = texture;
		uMin = 0;
		vMin = 0;
		uLen = 1;
		vLen = 1;
	}
	
	/**
	 * Inits texture and uv coords
	 */
	private void initTexture(TextureRegion textureRegion)
	{
		this.texture = textureRegion.getTexture();
		uMin = textureRegion.getU();
		vMin = textureRegion.getV();
		uLen = textureRegion.getU2() - uMin;
		vLen = textureRegion.getV2() - vMin;
	}
	
	/**
	 * Distributes coordinates equally throughout the grid
	 *
	 * @param x          x position of the bottom left corner of the grid
	 * @param y          y position of the bottom left corner of the grid
	 * @param width      width of texture
	 * @param height     height of texture
	 * @param gridWidth  width of grid where 3 would have 3 vertical lines
	 * @param gridHeight height of grid where 3 would have 3 horizontal lines
	 */
	private void fillGrid(float x, float y, float width, float height, int gridWidth, int gridHeight)
	{
		if (gridWidth < 2 || gridHeight < 2)
		{
			throw new IllegalArgumentException("Width or height smaller than 2. That can't make a quad");
		}
		grid = new Vector2[gridHeight][gridWidth];
		
		float widthSeparation = width / (gridWidth - 1f);
		float heightSeparation = height / (gridHeight - 1f);
		
		
		for (int h = 0; h < grid.length; h++)
		{
			float y1 = h * heightSeparation + y;
			for (int w = 0; w < grid[h].length; w++)
			{
				grid[h][w] = new Vector2(w * widthSeparation + x, y1);
			}
		}
	}
	
	/**
	 * Inits uv values
	 */
	private void initSpriteVertices()
	{
		//number of squares in grid * number of points in two triangles * number of elements in vertex
		spriteVertices = new float[(grid.length - 1) * (grid[0].length - 1) * NUM_VERTICES_PER_SQUARE * NUM_ELEMENTS_IN_VERTEX];
		
		//fills uv data here
		/*
		uv up is 0 and uv down is 1 while world coordinate is flipped
		 */
		float uSeparation = 1f / (grid[0].length - 1f);
		float vSeparation = 1f / (grid.length - 1f);
		verticalSeparation = (grid[0].length - 1) * NUM_VERTICES_PER_SQUARE - 3;
		
		for (int h = 0; h < grid.length; h++)
		{
			float v = (grid.length - 1 - h) * vSeparation * vLen + vMin;
			for (int w = 0; w < grid[h].length; w++)
			{
				float u = w * uSeparation * vLen + vMin;
				
				//position in top right square
				int indexTR = ((grid[0].length - 1) * h + w) * NUM_VERTICES_PER_SQUARE;
				int indexTL = indexTR - 3;
				int indexBR = indexTR - verticalSeparation;
				int indexBL = indexBR - 5;
				
				if (w > 0)
				{
					if (h < grid.length - 1)
					{
						put(indexTL * NUM_ELEMENTS_IN_VERTEX, U_VERTEX, u, V_VERTEX, v);
					}
					if (h > 0)
					{
						put(indexBL * NUM_ELEMENTS_IN_VERTEX, U_VERTEX, u, V_VERTEX, v);
					}
				}
				if (w < grid[h].length - 1)
				{
					if (h < grid.length - 1)
					{
						put(indexTR * NUM_ELEMENTS_IN_VERTEX, U_VERTEX, u, V_VERTEX, v);
					}
					if (h > 0)
					{
						put(indexBR * NUM_ELEMENTS_IN_VERTEX, U_VERTEX, u, V_VERTEX, v);
					}
				}
				
			}
		}
		
	}
	
	@Override
	public void render(SpriteBatch batch, float delta)
	{
		updateSpriteVertices(batch.getColor().toFloatBits());
		batch.draw(texture, spriteVertices, 0, spriteVertices.length);
	}
	
	/**
	 * Constructs the necessary render array with the grid points
	 *
	 * @param color color of batch
	 */
	private void updateSpriteVertices(float color)
	{
		//fills coord data and color data here
		/*
		spritebatch's triangles are defined as such
		0,1,2 = first triangle
		2,3,0 = second triangle
		
		
		3-2
		|/|
		0-1
		
		
		etc
		
		Therefore, most positions will be repeated. If I want to create a more optimized version, make a custom spritebatch
		and read about indices https://gamedev.stackexchange.com/questions/10727/fastest-way-to-draw-quads-in-opengl-es
		
		*/
		for (int h = 0; h < grid.length; h++)
		{
			for (int w = 0; w < grid[h].length; w++)
			{
				int indexTR = ((grid[0].length - 1) * h + w) * NUM_VERTICES_PER_SQUARE;
				int indexTL = indexTR - 3;
				int indexBR = indexTR - verticalSeparation;
				int indexBL = indexBR - 5;
				
				if (w > 0)
				{
					if (h < grid.length - 1)
					{
						put(indexTL * NUM_ELEMENTS_IN_VERTEX, X_VERTEX, grid[h][w].x, Y_VERTEX, grid[h][w].y, COLOR_VERTEX, color);
					}
					if (h > 0)
					{
						put(indexBL * NUM_ELEMENTS_IN_VERTEX, X_VERTEX, grid[h][w].x, Y_VERTEX, grid[h][w].y, COLOR_VERTEX, color);
					}
				}
				if (w < grid[h].length - 1)
				{
					if (h < grid.length - 1)
					{
						put(indexTR * NUM_ELEMENTS_IN_VERTEX, X_VERTEX, grid[h][w].x, Y_VERTEX, grid[h][w].y, COLOR_VERTEX, color);
					}
					if (h > 0)
					{
						put(indexBR * NUM_ELEMENTS_IN_VERTEX, X_VERTEX, grid[h][w].x, Y_VERTEX, grid[h][w].y, COLOR_VERTEX, color);
					}
				}
				
			}
		}
	}
	
	private void put(int index, int offset0, float value0, int offset1, float value1)
	{
		spriteVertices[index + offset0] = value0;
		spriteVertices[index + offset1] = value1;
	}
	
	private void put(int index, int offset0, float value0, int offset1, float value1, int offset2, float value2)
	{
		spriteVertices[index + offset0] = value0;
		spriteVertices[index + offset1] = value1;
		spriteVertices[index + offset2] = value2;
	}
	
	public Texture getTexture()
	{
		return texture;
	}
	
	public Vector2[][] getGrid()
	{
		return grid;
	}
	
	/**
	 * Adds to the grid
	 *
	 * @param adder how much to add
	 */
	public void addAll(Vector2 adder)
	{
		for (Vector2[] vectors : grid)
		{
			for (Vector2 vector : vectors)
			{
				vector.add(adder);
			}
		}
	}
	
	/**
	 * Flips the grid
	 *
	 * @param horizontal true if the grid should be flipped horizontally
	 * @param vertical   true if the grid should be flipped vertically
	 */
	public void flip(boolean horizontal, boolean vertical)
	{
		//todo lel
	}
}
